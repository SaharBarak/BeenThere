package com.beenthere.repositories

import com.beenthere.entities.RatingLandlordEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RatingLandlordRepository : CoroutineCrudRepository<RatingLandlordEntity, UUID>