package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("listing_photos")
data class ListingPhotoEntity(
    @Id
    val id: UUID? = null,
    
    @Column("listing_id")
    val listingId: UUID,
    
    @Column("url")
    val url: String,
    
    @Column("sort")
    val sort: Int
)