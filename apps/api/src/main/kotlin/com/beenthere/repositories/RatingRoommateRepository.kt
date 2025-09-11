package com.beenthere.repositories

import com.beenthere.entities.RatingRoommateEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RatingRoommateRepository : CoroutineCrudRepository<RatingRoommateEntity, UUID>