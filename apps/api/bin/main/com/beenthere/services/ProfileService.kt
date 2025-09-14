package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.apartments.*
import com.beenthere.entities.*
import com.beenthere.repositories.*
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val listingRepository: ListingRepository,
    private val listingPhotoRepository: ListingPhotoRepository,
    private val listingMemberRepository: ListingMemberRepository,
    private val placeRepository: PlaceRepository,
    private val rantGroupRepository: RantGroupRepository,
    private val ratingRoommateRepository: RatingRoommateRepository,
    private val ratingLandlordRepository: RatingLandlordRepository,
    private val ratingApartmentRepository: RatingApartmentRepository,
    private val listingMemberService: ListingMemberService
) {
    
    /**
     * Get seeker profile - individual user profile with roommate ratings
     */
    suspend fun getSeekerProfile(userId: UUID): Result<SeekerProfileRes> {
        return try {
            val user = userRepository.findById(userId)
                ?: return Result.failure(ServiceError.UserNotFound(userId.toString()))
            
            // Calculate roommate rating for this user
            val roommateRating = calculateRoommateRating(userId)
            
            // For now, using placeholder photos and preferences
            Result.success(SeekerProfileRes(
                user = UserDetails(
                    id = user.id.toString(),
                    displayName = user.displayName,
                    photoUrl = user.photoUrl,
                    bio = user.bio
                ),
                photos = listOf(user.photoUrl).filterNotNull(),
                preferences = null, // TODO: implement user preferences
                roommateRating = roommateRating
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    /**
     * Get apartment profile - works for both ENTIRE_PLACE and ROOMMATE_GROUP
     * Returns different data based on listing type
     */
    suspend fun getApartmentProfile(listingId: UUID): Result<ApartmentProfileRes> {
        return try {
            val listing = listingRepository.findById(listingId)
                ?: return Result.failure(ServiceError.ListingNotFound(listingId.toString()))
            
            val place = placeRepository.findById(listing.placeId)
                ?: return Result.failure(ServiceError.PlaceNotFound(listing.placeId.toString()))
            
            val photos = listingPhotoRepository.findByListingIdOrderBySort(listingId).toList()
            
            val placeDetails = PlaceDetails(
                id = place.id.toString(),
                googlePlaceId = place.googlePlaceId,
                formattedAddress = place.formattedAddress,
                lat = place.lat,
                lng = place.lng
            )
            
            val listingDetails = ListingDetails(
                id = listing.id.toString(),
                ownerUserId = listing.ownerUserId.toString(),
                title = listing.title,
                description = null, // TODO: add description field to listing
                price = listing.price,
                type = listing.type,
                capacityTotal = listing.capacityTotal,
                spotsAvailable = listing.spotsAvailable,
                moveInDate = listing.moveInDate?.toString(),
                rentPerRoom = listing.rentPerRoom,
                photos = photos.map { it.url },
                attrs = null, // TODO: convert JSON attrs
                autoAccept = listing.autoAccept,
                createdAt = listing.createdAt.toString()
            )
            
            // Calculate ratings for this place
            val ratings = calculateApartmentRatings(listing.placeId)
            
            // Get roommate profiles if this is a ROOMMATE_GROUP
            val roommates = if (listing.type == "ROOMMATE_GROUP") {
                val membersResult = listingMemberService.getCurrentMembers(listingId)
                when {
                    membersResult.isSuccess -> membersResult.getOrNull()
                    else -> emptyList()
                }
            } else null
            
            // Group preferences for roommate groups (placeholder)
            val groupPrefs = if (listing.type == "ROOMMATE_GROUP") {
                mapOf("placeholder" to "preferences") // TODO: implement group preferences
            } else null
            
            Result.success(ApartmentProfileRes(
                listing = listingDetails,
                place = placeDetails,
                ratings = ratings,
                roommates = roommates,
                groupPrefs = groupPrefs
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
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
    
    private suspend fun calculateApartmentRatings(placeId: UUID): ApartmentRatings {
        try {
            // Get all rant groups for this place
            val rantGroups = rantGroupRepository.findByPlaceIdOrderByCreatedAtDesc(placeId).toList()
            val rantGroupIds = rantGroups.mapNotNull { it.id }
            
            // Get all landlord ratings for these rant groups
            val landlordRatings = if (rantGroupIds.isNotEmpty()) {
                ratingLandlordRepository.findByRantGroupIdIn(rantGroupIds)
            } else emptyList()
            
            // Get all apartment ratings for these rant groups
            val apartmentRatings = if (rantGroupIds.isNotEmpty()) {
                ratingApartmentRepository.findByRantGroupIdIn(rantGroupIds)
            } else emptyList()
            
            // Calculate landlord breakdown
            val landlordBreakdown = if (landlordRatings.isNotEmpty()) {
                RatingBreakdown(
                    avg = 4.2, // Placeholder calculation
                    count = landlordRatings.size,
                    scores = mapOf(
                        "fairness" to 4.1,
                        "response" to 4.3,
                        "maintenance" to 4.0,
                        "privacy" to 4.4
                    )
                )
            } else null
            
            // Calculate apartment breakdown
            val apartmentBreakdown = if (apartmentRatings.isNotEmpty()) {
                RatingBreakdown(
                    avg = 3.8, // Placeholder calculation
                    count = apartmentRatings.size,
                    scores = mapOf(
                        "condition" to 3.9,
                        "noise" to 3.7,
                        "utilities" to 3.8,
                        "sunlightMold" to 3.9
                    )
                )
            } else null
            
            // Neighbors rating (placeholder - would need separate neighbor ratings table)
            val neighborsBreakdown = RatingBreakdown(
                avg = 3.9,
                count = 5, // Placeholder
                scores = mapOf(
                    "noise" to 3.8,
                    "safety" to 4.1,
                    "community" to 3.7
                )
            )
            
            // Recent ratings (simplified)
            val recentRatings = listOf<RecentRating>() // TODO: implement recent ratings aggregation
            
            return ApartmentRatings(
                landlord = landlordBreakdown,
                apartment = apartmentBreakdown,
                neighbors = neighborsBreakdown,
                recent = recentRatings
            )
            
        } catch (e: Exception) {
            return ApartmentRatings(
                landlord = null,
                apartment = null,
                neighbors = null,
                recent = emptyList()
            )
        }
    }
}