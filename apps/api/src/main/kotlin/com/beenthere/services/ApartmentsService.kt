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
            
            // Create listing
            val listing = ListingEntity(
                ownerUserId = ownerUserId,
                placeId = place.id!!,
                title = req.title,
                price = req.price,
                attrs = req.attrs?.let { objectMapper.valueToTree(it) },
                autoAccept = req.autoAccept ?: false
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
            
            // Get listings with pagination
            val listings = if (cursorInstant != null) {
                listingRepository.findActiveListingsAfterCursor(cursorInstant, limit)
            } else {
                listingRepository.findActiveListings(limit)
            }.toList()
            
            // Convert to DTOs
            val listingCards = listings.map { listing ->
                val photos = listingPhotoRepository.findByListingIdOrderBySort(listing.id!!).toList()
                
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
                    autoAccept = listing.autoAccept
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
            
            // Check for match conditions
            var matchId: String? = null
            if (req.action == ListingSwipeAction.LIKE) {
                // Match if autoAccept is true OR owner has already liked the seeker
                val shouldMatch = listing.autoAccept || 
                    listingSwipeRepository.findByUserIdAndListingId(listing.ownerUserId, listingId)?.action == "LIKE"
                
                if (shouldMatch) {
                    // Create match
                    val match = ListingMatchEntity(
                        userId = userId,
                        listingId = listingId
                    )
                    val savedMatch = listingMatchRepository.save(match)
                    matchId = savedMatch.id.toString()
                }
            }
            
            Result.success(ListingSwipeRes(matchId = matchId))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
}