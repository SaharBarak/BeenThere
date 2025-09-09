package com.beenthere.match

import com.beenthere.error.ServiceError
import com.michaelbull.result.Result
import com.michaelbull.result.Ok
import com.michaelbull.result.Err
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class MatchService(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) {

    fun createMatch(userId: String, landlordId: String, listingId: String): Mono<Result<MatchEntity, ServiceError>> {
        val match = MatchEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            landlordId = landlordId,
            listingId = listingId,
            status = "PENDING"
        )

        return r2dbcEntityTemplate.insert(match)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create match")))
    }

    fun getMatchesByUser(userId: String): Flux<MatchEntity> {
        return r2dbcEntityTemplate.select(MatchEntity::class.java)
            .matching(Query.query(Criteria.where("user_id").`is`(userId)))
            .all()
    }

    fun getActiveMatches(userId: String): Flux<MatchEntity> {
        return r2dbcEntityTemplate.select(MatchEntity::class.java)
            .matching(Query.query(
                Criteria.where("user_id").`is`(userId)
                    .and(Criteria.where("is_active").`is`(true))
            ))
            .all()
    }

    fun updateMatchStatus(matchId: String, status: String): Mono<Result<MatchEntity, ServiceError>> {
        return r2dbcEntityTemplate.select(MatchEntity::class.java)
            .matching(Query.query(Criteria.where("id").`is`(matchId)))
            .one()
            .flatMap { existingMatch ->
                val updatedMatch = existingMatch.copy(
                    status = status,
                    updatedAt = java.time.Instant.now()
                )
                r2dbcEntityTemplate.update(updatedMatch)
                    .map { Ok(updatedMatch) }
            }
            .onErrorReturn(Err(ServiceError.ResourceNotFound("Match not found")))
    }

    fun getMatchStats(userId: String): Mono<MatchStats> {
        return getMatchesByUser(userId)
            .collectList()
            .map { matches ->
                val pending = matches.count { it.status == "PENDING" }
                val accepted = matches.count { it.status == "ACCEPTED" }
                val rejected = matches.count { it.status == "REJECTED" }
                val expired = matches.count { it.status == "EXPIRED" }
                
                MatchStats(
                    totalMatches = matches.size,
                    pending = pending,
                    accepted = accepted,
                    rejected = rejected,
                    expired = expired
                )
            }
    }
}


data class MatchStats(
    val totalMatches: Int,
    val pending: Int,
    val accepted: Int,
    val rejected: Int,
    val expired: Int
)