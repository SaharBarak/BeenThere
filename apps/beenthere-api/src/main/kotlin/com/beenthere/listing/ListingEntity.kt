package com.beenthere.listing

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("listings")
data class ListingEntity(
    @Id
    val id: String,
    
    @Column("landlord_id")
    val landlordId: String,
    
    @Column("title")
    val title: String,
    
    @Column("description")
    val description: String,
    
    @Column("address")
    val address: String,
    
    @Column("city")
    val city: String,
    
    @Column("state")
    val state: String,
    
    @Column("zip_code")
    val zipCode: String,
    
    @Column("latitude")
    val latitude: BigDecimal,
    
    @Column("longitude")
    val longitude: BigDecimal,
    
    @Column("rent_amount")
    val rentAmount: BigDecimal,
    
    @Column("deposit_amount")
    val depositAmount: BigDecimal? = null,
    
    @Column("bedrooms")
    val bedrooms: Int,
    
    @Column("bathrooms")
    val bathrooms: BigDecimal,
    
    @Column("square_feet")
    val squareFeet: Int? = null,
    
    @Column("property_type")
    val propertyType: PropertyType,
    
    @Column("furnished")
    val furnished: Boolean = false,
    
    @Column("pet_friendly")
    val petFriendly: Boolean = false,
    
    @Column("smoking_allowed")
    val smokingAllowed: Boolean = false,
    
    @Column("utilities_included")
    val utilitiesIncluded: Boolean = false,
    
    @Column("parking_available")
    val parkingAvailable: Boolean = false,
    
    @Column("laundry_available")
    val laundryAvailable: Boolean = false,
    
    @Column("gym_available")
    val gymAvailable: Boolean = false,
    
    @Column("pool_available")
    val poolAvailable: Boolean = false,
    
    @Column("available_date")
    val availableDate: java.time.LocalDate,
    
    @Column("lease_duration_months")
    val leaseDurationMonths: Int? = null,
    
    @Column("images")
    val images: List<String> = emptyList(),
    
    @Column("is_active")
    val isActive: Boolean = true,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)

enum class PropertyType {
    APARTMENT,
    HOUSE,
    CONDO,
    TOWNHOUSE,
    STUDIO,
    ROOM
}
