package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.apartments.*
import com.beenthere.entities.*
import com.beenthere.repositories.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ApartmentsService(
    private val listingRepository: ListingRepository,
    private val listingPhotoRepository: ListingPhotoRepository,
    private val listingSwipeRepository: ListingSwipeRepository,
    private val listingMatchRepository: ListingMatchRepository,
    private val listingMemberRepository: ListingMemberRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val objectMapper: ObjectMapper
) {
    
    @Transactional
    suspend fun createListing(req: CreateListingReq, ownerUserId: UUID): Result<CreateListingRes> {
        return try {
            // Verify place exists
            val place = placeRepository.findById(UUID.fromString(req.placeId))
                ?: return Result.failure(ServiceError.PlaceNotFound(req.placeId))
            
            // Validate roommate group fields
            if (req.type == "ROOMMATE_GROUP") {
                require(req.capacityTotal != null && req.spotsAvailable != null) {
                    "Capacity and spots are required for roommate groups"
                }
            }
            
            // Create listing
            val listing = ListingEntity(
                ownerUserId = ownerUserId,
                placeId = place.id!!,
                title = req.title,
                price = req.price,
                attrs = req.attrs?.let { objectMapper.valueToTree(it) },
                autoAccept = req.autoAccept ?: false,
                type = req.type,
                capacityTotal = req.capacityTotal,
                spotsAvailable = req.spotsAvailable,
                moveInDate = req.moveInDate?.let { java.time.LocalDate.parse(it) },
                rentPerRoom = req.rentPerRoom
            )
            val savedListing = listingRepository.save(listing)
            
            // Save photos
            req.photos?.forEachIndexed { index, photoUrl ->
                listingPhotoRepository.save(ListingPhotoEntity(
                    listingId = savedListing.id!!,
                    url = photoUrl,
                    sort = index
                ))
            }
            
            // For ROOMMATE_GROUP, add creator as OWNER/admin
            if (req.type == "ROOMMATE_GROUP") {
                listingMemberRepository.save(ListingMemberEntity(
                    listingId = savedListing.id!!,
                    userId = ownerUserId,
                    role = MemberRole.OWNER.value,
                    displayOrder = 0
                ))
            }
            
            Result.success(CreateListingRes(id = savedListing.id.toString()))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    suspend fun getApartmentsFeed(
        cursor: String?,
        limit: Int = 50,
        filters: Map<String, String> = emptyMap()
    ): Result<ApartmentsFeedRes> {
        return try {
            // Parse cursor for pagination
            val cursorInstant = cursor?.let { Instant.parse(it) }
            
            // Extract type filter
            val typeFilter = filters["type"] // "ENTIRE_PLACE" or "ROOMMATE_GROUP"
            
            // Get listings with pagination and type filtering
            val listings = if (cursorInstant != null) {
                listingRepository.findActiveListingsAfterCursorAndType(cursorInstant, limit, typeFilter)
            } else {
                listingRepository.findActiveWithLimitDescAndType(limit, typeFilter)
            }.toList()
            
            // Convert to DTOs with enrichment
            val listingCards = listings.map { listing ->
                val photos = listingPhotoRepository.findByListingIdOrderBySort(listing.id!!).toList()
                
                // Get roommate bubbles for ROOMMATE_GROUP listings
                val roommates = if (listing.type == "ROOMMATE_GROUP") {
                    listingMemberRepository.findCurrentMembersByListingId(listing.id)
                        .take(5) // Limit to 5 bubbles
                        .map { member ->
                            val user = userRepository.findById(member.userId)!!
                            RoommateCard(
                                userId = member.userId.toString(),
                                displayName = user.displayName,
                                photoUrl = user.photoUrl,
                                role = member.role,
                                isAdmin = member.role == "OWNER"
                            )
                        }
                } else null
                
                // Calculate basic stats (simplified for now)
                val stats = ListingStats(
                    landlordAvg = null, // TODO: implement rating aggregation
                    landlordCount = 0,
                    apartmentAvg = null,
                    apartmentCount = 0,
                    neighborsAvg = null,
                    neighborsCount = 0,
                    roommatesAvg = null,
                    roommatesCount = roommates?.size ?: 0
                )
                
                ListingCard(
                    id = listing.id.toString(),
                    ownerUserId = listing.ownerUserId.toString(),
                    placeId = listing.placeId.toString(),
                    title = listing.title,
                    price = listing.price,
                    attrs = listing.attrs?.let { 
                        objectMapper.convertValue(it, Map::class.java) as Map<String, Any>
                    },
                    photos = photos.map { it.url },
                    createdAt = listing.createdAt.toString(),
                    autoAccept = listing.autoAccept,
                    type = listing.type,
                    capacityTotal = listing.capacityTotal,
                    spotsAvailable = listing.spotsAvailable,
                    moveInDate = listing.moveInDate?.toString(),
                    rentPerRoom = listing.rentPerRoom,
                    roommates = roommates,
                    stats = stats
                )
            }
            
            val nextCursor = if (listingCards.size == limit) {
                listings.lastOrNull()?.createdAt?.toString()
            } else null
            
            Result.success(ApartmentsFeedRes(
                items = listingCards,
                nextCursor = nextCursor
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    @Transactional
    suspend fun swipeOnListing(req: ListingSwipeReq, userId: UUID): Result<ListingSwipeRes> {
        return try {
            val listingId = UUID.fromString(req.listingId)
            
            // Check if listing exists and is active
            val listing = listingRepository.findById(listingId)
                ?: return Result.failure(ServiceError.ValidationError("Listing not found", "Listing with ID $listingId not found"))
            
            if (!listing.isActive) {
                return Result.failure(ServiceError.ValidationError("Listing is no longer active", "Listing is inactive"))
            }
            
            // Check if already swiped
            val existingSwipe = listingSwipeRepository.findByUserIdAndListingId(userId, listingId)
            if (existingSwipe != null) {
                return Result.failure(ServiceError.ValidationError("Already swiped on this listing", "User has already swiped on this listing"))
            }
            
            // Create swipe record
            val swipe = ListingSwipeEntity(
                userId = userId,
                listingId = listingId,
                action = req.action.name
            )
            listingSwipeRepository.save(swipe)
            
            // Check for match conditions based on listing type
            var matchId: String? = null
            if (req.action == ListingSwipeAction.LIKE) {
                when (listing.type) {
                    "ENTIRE_PLACE" -> {
                        // Traditional matching: autoAccept OR mutual like with owner
                        val shouldMatch = listing.autoAccept || 
                            listingSwipeRepository.findByUserIdAndListingId(listing.ownerUserId, listingId)?.action == "LIKE"
                        
                        if (shouldMatch) {
                            // Create match immediately
                            val match = ListingMatchEntity(
                                userId = userId,
                                listingId = listingId
                            )
                            val savedMatch = listingMatchRepository.save(match)
                            matchId = savedMatch.id.toString()
                        }
                    }
                    "ROOMMATE_GROUP" -> {
                        // Roommate group: seeker joins candidate queue
                        // Admin will review and vote via ListingMemberService.voteOnCandidate()
                        // No immediate match - seeker appears in admin's candidates list
                        // Match only created when admin votes LIKE
                        matchId = null // No immediate match for roommate groups
                    }
                    else -> {
                        // Default to ENTIRE_PLACE behavior
                        val shouldMatch = listing.autoAccept || 
                            listingSwipeRepository.findByUserIdAndListingId(listing.ownerUserId, listingId)?.action == "LIKE"
                        
                        if (shouldMatch) {
                            val match = ListingMatchEntity(
                                userId = userId,
                                listingId = listingId
                            )
                            val savedMatch = listingMatchRepository.save(match)
                            matchId = savedMatch.id.toString()
                        }
                    }
                }
            }
            
            Result.success(ListingSwipeRes(matchId = matchId))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
}