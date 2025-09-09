package com.beenthere.feed

import com.beenthere.error.ServiceError
import com.beenthere.listing.ListingEntity
import com.beenthere.listing.ListingRepo
import com.beenthere.listing.PropertyType
import com.beenthere.listing.toDto
import com.michaelbull.result.Result
import com.michaelbull.result.Ok
import com.michaelbull.result.Err
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class FeedService(
    private val listingRepo: ListingRepo
) {

    fun getFeed(mode: String, cursor: String?): Mono<Result<FeedResponse, ServiceError>> {
        return when (mode) {
            "houses" -> getHouseListings(cursor)
            "roommates" -> getRoommateListings(cursor)
            else -> Mono.just(Err(ServiceError.ValidationError("Invalid mode. Must be 'houses' or 'roommates'")))
        }
    }

    private fun getHouseListings(cursor: String?): Mono<Result<FeedResponse, ServiceError>> {
        return try {
            listingRepo.findAllActive()
                .collectList()
                .map { listings ->
                    val sortedListings = listings
                        .filter { it.propertyType in listOf(PropertyType.APARTMENT, PropertyType.HOUSE, PropertyType.CONDO, PropertyType.TOWNHOUSE) }
                        .sortedByDescending { it.createdAt }
                    
                    val paginatedListings = if (cursor != null) {
                        val cursorIndex = sortedListings.indexOfFirst { it.id == cursor }
                        if (cursorIndex >= 0) {
                            sortedListings.drop(cursorIndex + 1).take(20)
                        } else {
                            sortedListings.take(20)
                        }
                    } else {
                        sortedListings.take(20)
                    }
                    
                    val nextCursor = if (paginatedListings.size == 20) {
                        paginatedListings.lastOrNull()?.id
                    } else null
                    
                    val feedResponse = FeedResponse(
                        items = paginatedListings.map { it.toDto() },
                        cursor = nextCursor
                    )
                    
                    Ok(feedResponse)
                }
        } catch (e: Exception) {
            Mono.just(Err(ServiceError.InternalServerError("Failed to fetch house listings: ${e.message}")))
        }
    }

    private fun getRoommateListings(cursor: String?): Mono<Result<FeedResponse, ServiceError>> {
        return try {
            listingRepo.findAllActive()
                .collectList()
                .map { listings ->
                    val sortedListings = listings
                        .filter { it.propertyType == PropertyType.ROOM }
                        .sortedByDescending { it.createdAt }
                    
                    val paginatedListings = if (cursor != null) {
                        val cursorIndex = sortedListings.indexOfFirst { it.id == cursor }
                        if (cursorIndex >= 0) {
                            sortedListings.drop(cursorIndex + 1).take(20)
                        } else {
                            sortedListings.take(20)
                        }
                    } else {
                        sortedListings.take(20)
                    }
                    
                    val nextCursor = if (paginatedListings.size == 20) {
                        paginatedListings.lastOrNull()?.id
                    } else null
                    
                    val feedResponse = FeedResponse(
                        items = paginatedListings.map { it.toDto() },
                        cursor = nextCursor
                    )
                    
                    Ok(feedResponse)
                }
        } catch (e: Exception) {
            Mono.just(Err(ServiceError.InternalServerError("Failed to fetch roommate listings: ${e.message}")))
        }
    }

}

data class FeedResponse(
    val items: List<ListingDto>,
    val cursor: String?
)

data class ListingDto(
    val id: String,
    val title: String,
    val description: String,
    val address: String,
    val city: String,
    val state: String,
    val rentAmount: java.math.BigDecimal,
    val bedrooms: Int,
    val bathrooms: java.math.BigDecimal,
    val propertyType: String,
    val images: List<String>,
    val createdAt: java.time.Instant
)

fun ListingEntity.toDto(): ListingDto = ListingDto(
    id = this.id,
    title = this.title,
    description = this.description,
    address = this.address,
    city = this.city,
    state = this.state,
    rentAmount = this.rentAmount,
    bedrooms = this.bedrooms,
    bathrooms = this.bathrooms,
    propertyType = this.propertyType.name,
    images = this.images,
    createdAt = this.createdAt
)
