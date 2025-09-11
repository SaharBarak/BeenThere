package com.beenthere.repositories

import com.beenthere.entities.RatingApartmentEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RatingApartmentRepository : CoroutineCrudRepository<RatingApartmentEntity, UUID>