package com.beenthere.subscription

import com.beenthere.error.ServiceError
import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class SubscriptionService(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    fun getSubscriptionStatus(userId: String): Mono<Result<SubscriptionDto, ServiceError>> {
        return r2dbcEntityTemplate.select(SubscriptionEntity::class.java)
            .matching(Query.query(Criteria.where("user_id").`is`(userId)))
            .one()
            .map { subscription ->
                Ok(subscription.toDto())
            }
            .onErrorReturn(Err(ServiceError.ResourceNotFound("No subscription found")))
    }

    fun createSubscription(
        userId: String,
        planId: String,
    ): Mono<Result<SubscriptionDto, ServiceError>> {
        val subscription =
            SubscriptionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                planId = planId,
                status = "active",
                periodEnd = java.time.Instant.now().plusSeconds(30 * 24 * 60 * 60), // 30 days
            )

        return r2dbcEntityTemplate.insert(subscription)
            .map { Ok(it.toDto()) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create subscription")))
    }

    fun updateSubscriptionStatus(
        userId: String,
        status: String,
    ): Mono<Result<SubscriptionDto, ServiceError>> {
        return r2dbcEntityTemplate.select(SubscriptionEntity::class.java)
            .matching(Query.query(Criteria.where("user_id").`is`(userId)))
            .one()
            .flatMap { existingSubscription ->
                val updatedSubscription =
                    existingSubscription.copy(
                        status = status,
                        updatedAt = java.time.Instant.now(),
                    )
                r2dbcEntityTemplate.update(updatedSubscription)
                    .map { Ok(updatedSubscription.toDto()) }
            }
            .onErrorReturn(Err(ServiceError.ResourceNotFound("Subscription not found")))
    }
}

data class SubscriptionEntity(
    val id: String,
    val userId: String,
    val planId: String,
    val status: String,
    val periodEnd: java.time.Instant,
    val createdAt: java.time.Instant = java.time.Instant.now(),
    val updatedAt: java.time.Instant = java.time.Instant.now(),
)

data class SubscriptionDto(
    val status: String,
    val periodEnd: java.time.Instant? = null,
    val planId: String? = null,
)

fun SubscriptionEntity.toDto(): SubscriptionDto =
    SubscriptionDto(
        status = this.status,
        periodEnd = this.periodEnd,
        planId = this.planId,
    )
