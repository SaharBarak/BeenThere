package com.beenthere.repositories

import com.beenthere.entities.ListingPhotoEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface ListingPhotoRepository : CoroutineCrudRepository<ListingPhotoEntity, UUID> {
    
    fun findByListingIdOrderBySort(listingId: UUID): Flow<ListingPhotoEntity>
    
    suspend fun deleteByListingId(listingId: UUID)
}