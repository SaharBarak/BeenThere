package com.beenthere.swipe

import com.beenthere.error.ServiceError
import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class SwipeService(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    fun createSwipe(request: CreateSwipeRequest): Mono<Result<SwipeResponse, ServiceError>> {
        val userId = "current-user-id" // This would come from security context

        return hasUserSwipedOnTarget(userId, request.targetType, request.targetId)
            .flatMap { hasSwiped ->
                if (hasSwiped) {
                    Mono.just(Err(ServiceError.DuplicateSwipe("User has already swiped on this target")))
                } else {
                    val swipe =
                        SwipeEntity(
                            id = UUID.randomUUID().toString(),
                            swiperId = userId,
                            targetType = request.targetType,
                            targetId = request.targetId,
                            action = request.action,
                        )

                    r2dbcEntityTemplate.insert(swipe)
                        .flatMap { savedSwipe ->
                            // Check for potential match if it's a like
                            if (request.action == "like") {
                                checkForMatch(request.targetType, request.targetId)
                                    .map { matchId ->
                                        Ok(SwipeResponse(matchId = matchId))
                                    }
                            } else {
                                Mono.just(Ok(SwipeResponse()))
                            }
                        }
                }
            }.onErrorReturn(Err(ServiceError.InternalServerError("Failed to create swipe")))
    }

    private fun checkForMatch(
        targetType: String,
        targetId: String,
    ): Mono<String?> {
        return when (targetType) {
            "listing" -> {
                // Check if landlord has liked this user back
                // This is a simplified implementation - in reality you'd check if the landlord
                // has liked any of the user's listings or if there's mutual interest
                Mono.just(null) // No match for now
            }
            "user" -> {
                // Check if user has liked this user back
                // This is a simplified implementation
                Mono.just(null) // No match for now
            }
            else -> Mono.just(null)
        }
    }

    fun getUserSwipes(userId: String): Flux<SwipeEntity> {
        return r2dbcEntityTemplate.select(SwipeEntity::class.java)
            .matching(Query.query(Criteria.where("swiper_id").`is`(userId)))
            .all()
    }

    fun getTargetSwipes(
        targetType: String,
        targetId: String,
    ): Flux<SwipeEntity> {
        return r2dbcEntityTemplate.select(SwipeEntity::class.java)
            .matching(
                Query.query(
                    Criteria.where("target_type").`is`(targetType)
                        .and(Criteria.where("target_id").`is`(targetId)),
                ),
            )
            .all()
    }

    fun hasUserSwipedOnTarget(
        userId: String,
        targetType: String,
        targetId: String,
    ): Mono<Boolean> {
        return r2dbcEntityTemplate.select(SwipeEntity::class.java)
            .matching(
                Query.query(
                    Criteria.where("swiper_id").`is`(userId)
                        .and(Criteria.where("target_type").`is`(targetType))
                        .and(Criteria.where("target_id").`is`(targetId)),
                ),
            )
            .one()
            .map { true }
            .onErrorReturn(false)
    }

    fun getSwipeStats(userId: String): Mono<SwipeStats> {
        return getUserSwipes(userId)
            .collectList()
            .map { swipes ->
                val likes = swipes.count { it.action == "like" }
                val passes = swipes.count { it.action == "pass" }

                SwipeStats(
                    totalSwipes = swipes.size,
                    likes = likes,
                    passes = passes,
                )
            }
    }
}

data class SwipeStats(
    val totalSwipes: Int,
    val likes: Int,
    val passes: Int,
)

data class CreateSwipeRequest(
    val targetType: String, // "listing" or "user"
    val targetId: String, // UUID
    val action: String, // "like" or "pass"
)

data class SwipeResponse(
    val matchId: String? = null,
)
