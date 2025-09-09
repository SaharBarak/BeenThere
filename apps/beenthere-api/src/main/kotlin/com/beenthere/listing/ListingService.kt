package com.beenthere.listing

import com.beenthere.error.ServiceError
import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@Service
class ListingService(
    private val listingRepo: ListingRepo,
) {
    fun createListing(
        landlordId: String,
        request: CreateListingRequest,
    ): Mono<Result<ListingEntity, ServiceError>> {
        val listing =
            ListingEntity(
                id = UUID.randomUUID().toString(),
                landlordId = landlordId,
                title = request.title,
                description = request.description,
                address = request.address,
                city = request.city,
                state = request.state,
                zipCode = request.zipCode,
                latitude = request.latitude,
                longitude = request.longitude,
                rentAmount = request.rentAmount,
                depositAmount = request.depositAmount,
                bedrooms = request.bedrooms,
                bathrooms = request.bathrooms,
                squareFeet = request.squareFeet,
                propertyType = request.propertyType,
                furnished = request.furnished,
                petFriendly = request.petFriendly,
                smokingAllowed = request.smokingAllowed,
                utilitiesIncluded = request.utilitiesIncluded,
                parkingAvailable = request.parkingAvailable,
                laundryAvailable = request.laundryAvailable,
                gymAvailable = request.gymAvailable,
                poolAvailable = request.poolAvailable,
                availableDate = request.availableDate,
                leaseDurationMonths = request.leaseDurationMonths,
                images = request.images,
            )

        return listingRepo.save(listing)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create listing")))
    }

    fun findById(id: String): Mono<Result<ListingEntity, ServiceError>> {
        return listingRepo.findActiveById(id)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.ListingNotFound("Listing with id $id not found")))
    }

    fun findAll(): Flux<ListingEntity> {
        return listingRepo.findAllActive()
    }

    fun findByLandlordId(landlordId: String): Flux<ListingEntity> {
        return listingRepo.findByLandlordId(landlordId)
    }

    fun searchListings(criteria: SearchCriteria): Flux<ListingEntity> {
        return when {
            criteria.city != null && criteria.state != null -> {
                listingRepo.findByLocation(criteria.city, criteria.state)
            }
            criteria.minRent != null && criteria.maxRent != null -> {
                listingRepo.findByRentRange(criteria.minRent, criteria.maxRent)
            }
            criteria.bedrooms != null -> {
                listingRepo.findByBedrooms(criteria.bedrooms)
            }
            criteria.propertyType != null -> {
                listingRepo.findByPropertyType(criteria.propertyType)
            }
            else -> {
                listingRepo.findAllActive()
            }
        }
    }

    fun updateListing(
        id: String,
        landlordId: String,
        request: UpdateListingRequest,
    ): Mono<Result<ListingEntity, ServiceError>> {
        return listingRepo.findActiveById(id)
            .flatMap { existingListing ->
                if (existingListing.landlordId != landlordId) {
                    Mono.just(Err(ServiceError.UnauthorizedListingAccess("You can only update your own listings")))
                } else {
                    val updatedListing =
                        existingListing.copy(
                            title = request.title ?: existingListing.title,
                            description = request.description ?: existingListing.description,
                            address = request.address ?: existingListing.address,
                            city = request.city ?: existingListing.city,
                            state = request.state ?: existingListing.state,
                            zipCode = request.zipCode ?: existingListing.zipCode,
                            latitude = request.latitude ?: existingListing.latitude,
                            longitude = request.longitude ?: existingListing.longitude,
                            rentAmount = request.rentAmount ?: existingListing.rentAmount,
                            depositAmount = request.depositAmount ?: existingListing.depositAmount,
                            bedrooms = request.bedrooms ?: existingListing.bedrooms,
                            bathrooms = request.bathrooms ?: existingListing.bathrooms,
                            squareFeet = request.squareFeet ?: existingListing.squareFeet,
                            propertyType = request.propertyType ?: existingListing.propertyType,
                            furnished = request.furnished ?: existingListing.furnished,
                            petFriendly = request.petFriendly ?: existingListing.petFriendly,
                            smokingAllowed = request.smokingAllowed ?: existingListing.smokingAllowed,
                            utilitiesIncluded = request.utilitiesIncluded ?: existingListing.utilitiesIncluded,
                            parkingAvailable = request.parkingAvailable ?: existingListing.parkingAvailable,
                            laundryAvailable = request.laundryAvailable ?: existingListing.laundryAvailable,
                            gymAvailable = request.gymAvailable ?: existingListing.gymAvailable,
                            poolAvailable = request.poolAvailable ?: existingListing.poolAvailable,
                            availableDate = request.availableDate ?: existingListing.availableDate,
                            leaseDurationMonths = request.leaseDurationMonths ?: existingListing.leaseDurationMonths,
                            images = request.images ?: existingListing.images,
                            updatedAt = java.time.Instant.now(),
                        )

                    listingRepo.save(updatedListing)
                        .map { Ok(it) }
                }
            }
            .onErrorReturn(Err(ServiceError.ListingNotFound("Listing with id $id not found")))
    }

    fun deactivateListing(
        id: String,
        landlordId: String,
    ): Mono<Result<Unit, ServiceError>> {
        return listingRepo.findActiveById(id)
            .flatMap { existingListing ->
                if (existingListing.landlordId != landlordId) {
                    Mono.just(Err(ServiceError.UnauthorizedListingAccess("You can only deactivate your own listings")))
                } else {
                    val deactivatedListing =
                        existingListing.copy(
                            isActive = false,
                            updatedAt = java.time.Instant.now(),
                        )
                    listingRepo.save(deactivatedListing)
                        .map { Ok(Unit) }
                }
            }
            .onErrorReturn(Err(ServiceError.ListingNotFound("Listing with id $id not found")))
    }
}

data class CreateListingRequest(
    val title: String,
    val description: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val rentAmount: BigDecimal,
    val depositAmount: BigDecimal? = null,
    val bedrooms: Int,
    val bathrooms: BigDecimal,
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
)

data class UpdateListingRequest(
    val title: String? = null,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val rentAmount: BigDecimal? = null,
    val depositAmount: BigDecimal? = null,
    val bedrooms: Int? = null,
    val bathrooms: BigDecimal? = null,
    val squareFeet: Int? = null,
    val propertyType: PropertyType? = null,
    val furnished: Boolean? = null,
    val petFriendly: Boolean? = null,
    val smokingAllowed: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val parkingAvailable: Boolean? = null,
    val laundryAvailable: Boolean? = null,
    val gymAvailable: Boolean? = null,
    val poolAvailable: Boolean? = null,
    val availableDate: java.time.LocalDate? = null,
    val leaseDurationMonths: Int? = null,
    val images: List<String>? = null,
)

data class SearchCriteria(
    val city: String? = null,
    val state: String? = null,
    val minRent: BigDecimal? = null,
    val maxRent: BigDecimal? = null,
    val bedrooms: Int? = null,
    val propertyType: PropertyType? = null,
)
