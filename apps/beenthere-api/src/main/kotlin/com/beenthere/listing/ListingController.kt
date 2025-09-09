package com.beenthere.listing

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.toApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/listings")
class ListingController(
    private val listingService: ListingService,
) {
    @PostMapping
    fun createListing(
        @AuthenticationPrincipal landlordId: String,
        @Valid @RequestBody request: CreateListingRequest,
    ): Mono<ResponseEntity<ApiResponse<ListingDto>>> {
        return listingService.createListing(landlordId, request)
            .map { result ->
                result.fold(
                    success = { listing ->
                        ApiResponse.success(listing.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<ListingDto>().toResponseEntity()
                    },
                )
            }
    }

    @GetMapping
    fun getAllListings(): Flux<ResponseEntity<ApiResponse<List<ListingDto>>>> {
        return listingService.findAll()
            .map { it.toDto() }
            .collectList()
            .map { listings ->
                ResponseEntity.ok(ApiResponse.success(listings))
            }
    }

    @GetMapping("/search")
    fun searchListings(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = false) minRent: java.math.BigDecimal?,
        @RequestParam(required = false) maxRent: java.math.BigDecimal?,
        @RequestParam(required = false) bedrooms: Int?,
        @RequestParam(required = false) propertyType: PropertyType?,
    ): Flux<ResponseEntity<ApiResponse<List<ListingDto>>>> {
        val criteria =
            SearchCriteria(
                city = city,
                state = state,
                minRent = minRent,
                maxRent = maxRent,
                bedrooms = bedrooms,
                propertyType = propertyType,
            )

        return listingService.searchListings(criteria)
            .map { it.toDto() }
            .collectList()
            .map { listings ->
                ResponseEntity.ok(ApiResponse.success(listings))
            }
    }

    @GetMapping("/{id}")
    fun getListingById(
        @PathVariable id: String,
    ): Mono<ResponseEntity<ApiResponse<ListingDto>>> {
        return listingService.findById(id)
            .map { result ->
                result.fold(
                    success = { listing ->
                        ApiResponse.success(listing.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<ListingDto>().toResponseEntity()
                    },
                )
            }
    }

    @GetMapping("/my-listings")
    fun getMyListings(
        @AuthenticationPrincipal landlordId: String,
    ): Flux<ResponseEntity<ApiResponse<List<ListingDto>>>> {
        return listingService.findByLandlordId(landlordId)
            .map { it.toDto() }
            .collectList()
            .map { listings ->
                ResponseEntity.ok(ApiResponse.success(listings))
            }
    }

    @PutMapping("/{id}")
    fun updateListing(
        @PathVariable id: String,
        @AuthenticationPrincipal landlordId: String,
        @Valid @RequestBody request: UpdateListingRequest,
    ): Mono<ResponseEntity<ApiResponse<ListingDto>>> {
        return listingService.updateListing(id, landlordId, request)
            .map { result ->
                result.fold(
                    success = { listing ->
                        ApiResponse.success(listing.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<ListingDto>().toResponseEntity()
                    },
                )
            }
    }

    @DeleteMapping("/{id}")
    fun deactivateListing(
        @PathVariable id: String,
        @AuthenticationPrincipal landlordId: String,
    ): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return listingService.deactivateListing(id, landlordId)
            .map { result ->
                result.fold(
                    success = {
                        ApiResponse.success(Unit).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<Unit>().toResponseEntity()
                    },
                )
            }
    }
}

data class ListingDto(
    val id: String,
    val landlordId: String,
    val title: String,
    val description: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val latitude: java.math.BigDecimal,
    val longitude: java.math.BigDecimal,
    val rentAmount: java.math.BigDecimal,
    val depositAmount: java.math.BigDecimal? = null,
    val bedrooms: Int,
    val bathrooms: java.math.BigDecimal,
    val squareFeet: Int? = null,
    val propertyType: PropertyType,
    val furnished: Boolean = false,
    val petFriendly: Boolean = false,
    val smokingAllowed: Boolean = false,
    val utilitiesIncluded: Boolean = false,
    val parkingAvailable: Boolean = false,
    val laundryAvailable: Boolean = false,
    val gymAvailable: Boolean = false,
    val poolAvailable: Boolean = false,
    val availableDate: java.time.LocalDate,
    val leaseDurationMonths: Int? = null,
    val images: List<String> = emptyList(),
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant,
)

fun ListingEntity.toDto(): ListingDto =
    ListingDto(
        id = this.id,
        landlordId = this.landlordId,
        title = this.title,
        description = this.description,
        address = this.address,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode,
        latitude = this.latitude,
        longitude = this.longitude,
        rentAmount = this.rentAmount,
        depositAmount = this.depositAmount,
        bedrooms = this.bedrooms,
        bathrooms = this.bathrooms,
        squareFeet = this.squareFeet,
        propertyType = this.propertyType,
        furnished = this.furnished,
        petFriendly = this.petFriendly,
        smokingAllowed = this.smokingAllowed,
        utilitiesIncluded = this.utilitiesIncluded,
        parkingAvailable = this.parkingAvailable,
        laundryAvailable = this.laundryAvailable,
        gymAvailable = this.gymAvailable,
        poolAvailable = this.poolAvailable,
        availableDate = this.availableDate,
        leaseDurationMonths = this.leaseDurationMonths,
        images = this.images,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
