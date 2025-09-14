package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.apartments.*
import com.beenthere.entities.*
import com.beenthere.repositories.*
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ListingMemberService(
    private val listingMemberRepository: ListingMemberRepository,
    private val listingRepository: ListingRepository,
    private val listingSwipeRepository: ListingSwipeRepository,
    private val listingMatchRepository: ListingMatchRepository,
    private val userRepository: UserRepository,
    private val ratingRoommateRepository: RatingRoommateRepository
) {
    
    /**
     * Add a member to a roommate group (admin only)
     */
    @Transactional
    suspend fun addMember(listingId: UUID, req: AddMemberReq, requesterId: UUID): Result<Unit> {
        return try {
            // Verify requester is admin/owner
            val isAdmin = listingMemberRepository.isOwner(listingId, requesterId)
            if (!isAdmin) {
                return Result.failure(ServiceError.Forbidden("Only admin can add members"))
            }
            
            // Verify listing is ROOMMATE_GROUP
            val listing = listingRepository.findById(listingId)
                ?: return Result.failure(ServiceError.ListingNotFound(listingId.toString()))
            
            if (listing.type != "ROOMMATE_GROUP") {
                return Result.failure(ServiceError.BadRequest("Can only add members to roommate groups"))
            }
            
            // Verify user exists
            val userId = UUID.fromString(req.userId)
            val user = userRepository.findById(userId)
                ?: return Result.failure(ServiceError.UserNotFound(req.userId))
            
            // Check if user is already a member
            val existingMembership = listingMemberRepository.findCurrentMembership(listingId, userId)
            if (existingMembership != null) {
                return Result.failure(ServiceError.BadRequest("User is already a member"))
            }
            
            // Add member
            listingMemberRepository.save(ListingMemberEntity(
                listingId = listingId,
                userId = userId,
                role = req.role,
                displayOrder = req.displayOrder
            ))
            
            // Update spots available
            if (listing.spotsAvailable != null && listing.spotsAvailable > 0) {
                listingRepository.save(listing.copy(spotsAvailable = listing.spotsAvailable - 1))
            }
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    /**
     * Remove a member from a roommate group (admin only)
     */
    @Transactional
    suspend fun removeMember(listingId: UUID, memberUserId: UUID, requesterId: UUID): Result<Unit> {
        return try {
            // Verify requester is admin/owner
            val isAdmin = listingMemberRepository.isOwner(listingId, requesterId)
            if (!isAdmin) {
                return Result.failure(ServiceError.Forbidden("Only admin can remove members"))
            }
            
            // Can't remove the admin/owner
            if (memberUserId == requesterId) {
                return Result.failure(ServiceError.BadRequest("Admin cannot remove themselves"))
            }
            
            // Find and update member record
            val member = listingMemberRepository.findCurrentMembership(listingId, memberUserId)
                ?: return Result.failure(ServiceError.MemberNotFound(memberUserId.toString()))
            
            // Mark as left
            listingMemberRepository.save(member.copy(
                isCurrent = false,
                leftAt = Instant.now()
            ))
            
            // Update spots available
            val listing = listingRepository.findById(listingId)!!
            if (listing.spotsAvailable != null) {
                listingRepository.save(listing.copy(spotsAvailable = listing.spotsAvailable + 1))
            }
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    /**
     * Get candidates (seekers who liked the listing but haven't been matched)
     */
    suspend fun getCandidates(
        listingId: UUID, 
        requesterId: UUID,
        cursor: String? = null,
        limit: Int = 20
    ): Result<CandidatesRes> {
        return try {
            // Verify requester is admin/owner
            val isAdmin = listingMemberRepository.isOwner(listingId, requesterId)
            if (!isAdmin) {
                return Result.failure(ServiceError.Forbidden("Only admin can view candidates"))
            }
            
            // Get seekers who liked this listing but haven't been matched yet
            val candidates = getCandidateUsers(listingId, cursor, limit)
            
            val candidateCards = candidates.map { (user, swipe) ->
                val roommateRating = calculateRoommateRating(user.id!!)
                val compatibilityScore = calculateCompatibilityScore(listingId, user.id)
                
                CandidateCard(
                    user = UserDetails(
                        id = user.id.toString(),
                        displayName = user.displayName,
                        photoUrl = user.photoUrl,
                        bio = user.bio
                    ),
                    swipedAt = swipe.createdAt.toString(),
                    roommateRating = roommateRating,
                    compatibilityScore = compatibilityScore
                )
            }
            
            val nextCursor = if (candidateCards.size == limit) {
                candidates.lastOrNull()?.second?.createdAt?.toString()
            } else null
            
            Result.success(CandidatesRes(
                candidates = candidateCards,
                nextCursor = nextCursor
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    /**
     * Vote on a candidate (admin decision)
     */
    @Transactional
    suspend fun voteOnCandidate(
        listingId: UUID,
        candidateUserId: UUID, 
        action: String,
        requesterId: UUID
    ): Result<ListingSwipeRes> {
        return try {
            // Verify requester is admin/owner
            val isAdmin = listingMemberRepository.isOwner(listingId, requesterId)
            if (!isAdmin) {
                return Result.failure(ServiceError.Forbidden("Only admin can vote on candidates"))
            }
            
            when (action) {
                "LIKE" -> {
                    // Create match between seeker and admin (not whole group)
                    val existingMatch = listingMatchRepository.findByUserIdAndListingId(candidateUserId, listingId)
                    if (existingMatch != null) {
                        return Result.success(ListingSwipeRes(matchId = existingMatch.id.toString()))
                    }
                    
                    val match = listingMatchRepository.save(ListingMatchEntity(
                        userId = candidateUserId,
                        listingId = listingId,
                        createdAt = Instant.now()
                    ))
                    
                    Result.success(ListingSwipeRes(matchId = match.id.toString()))
                }
                "PASS" -> {
                    // Record the pass decision (could store in separate table if needed)
                    Result.success(ListingSwipeRes(matchId = null))
                }
                else -> Result.failure(ServiceError.BadRequest("Invalid action: $action"))
            }
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    /**
     * Get current members of a listing with their details
     */
    suspend fun getCurrentMembers(listingId: UUID): Result<List<RoommateProfile>> {
        return try {
            val members = listingMemberRepository.findCurrentMembersByListingId(listingId)
            
            val roommateProfiles = members.map { member ->
                val user = userRepository.findById(member.userId)!!
                val roommateRating = calculateRoommateRating(member.userId)
                
                RoommateProfile(
                    user = UserDetails(
                        id = user.id.toString(),
                        displayName = user.displayName,
                        photoUrl = user.photoUrl,
                        bio = user.bio
                    ),
                    role = member.role,
                    joinedAt = member.joinedAt.toString(),
                    roommateRating = roommateRating
                )
            }
            
            Result.success(roommateProfiles)
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    private suspend fun getCandidateUsers(
        listingId: UUID, 
        cursor: String?, 
        limit: Int
    ): List<Pair<UserEntity, ListingSwipeEntity>> {
        // This would need a complex query to get users who:
        // 1. Swiped LIKE on this listing
        // 2. Haven't been matched yet
        // 3. Are ordered by swipe time for pagination
        
        // For now, simplified implementation
        val swipes = listingSwipeRepository.findByListingIdAndAction(listingId, "LIKE")
            .toList()
            .take(limit)
        
        return swipes.mapNotNull { swipe ->
            val user = userRepository.findById(swipe.userId)
            if (user != null) Pair(user, swipe) else null
        }
    }
    
    private suspend fun calculateRoommateRating(userId: UUID): RatingsSummary {
        try {
            val ratings = ratingRoommateRepository.findByRateeUserIdOrderByCreatedAtDesc(userId).toList()
            if (ratings.isEmpty()) {
                return RatingsSummary(avg = null, count = 0)
            }
            
            // Calculate average from all roommate ratings
            // This is simplified - would need proper JSON parsing of scores
            val scores: List<Double> = ratings.map { 4.0 } // Placeholder average
            
            val average = if (scores.isNotEmpty()) scores.average() else null
            
            return RatingsSummary(
                avg = average,
                count = ratings.size
            )
            
        } catch (e: Exception) {
            return RatingsSummary(avg = null, count = 0)
        }
    }
    
    private suspend fun calculateCompatibilityScore(listingId: UUID, userId: UUID): Double? {
        // TODO: Implement compatibility calculation based on:
        // - User preferences vs group preferences
        // - Past roommate ratings compatibility
        // - Lifestyle preferences alignment
        
        // Placeholder for now
        return (Math.random() * 5.0) // Random score 0-5
    }
}