package com.beenthere.gateways

import com.beenthere.accessors.StripeAccessor
import org.springframework.stereotype.Component

/**
 * Stripe Gateway - Domain interface for billing and subscription operations
 * Wraps StripeAccessor and provides domain-specific methods for BeenThere billing
 */
@Component
class StripeGateway(
    private val stripeAccessor: StripeAccessor
) {
    
    /**
     * Create customer for BeenThere user
     */
    suspend fun createUserCustomer(
        userId: String,
        email: String,
        name: String
    ): Result<String> {
        return try {
            val customer = stripeAccessor.createCustomer(
                email = email,
                name = name,
                metadata = mapOf("beenthere_user_id" to userId)
            )
            Result.success(customer.id)
        } catch (e: Exception) {
            Result.failure(Exception("Stripe customer creation failed: ${e.message}", e))
        }
    }
    
    /**
     * Create payment intent for BeenThere subscription (₪20/month)
     */
    suspend fun createSubscriptionPayment(
        customerId: String,
        userId: String
    ): Result<String> {
        return try {
            val paymentIntent = stripeAccessor.createPaymentIntent(
                amount = 2000L, // ₪20 in agorot (Israeli currency cents)
                currency = "ils",
                customerId = customerId,
                metadata = mapOf(
                    "beenthere_user_id" to userId,
                    "subscription_type" to "premium_monthly"
                )
            )
            Result.success(paymentIntent.clientSecret)
        } catch (e: Exception) {
            Result.failure(Exception("Stripe payment intent creation failed: ${e.message}", e))
        }
    }
    
    /**
     * Create BeenThere premium subscription
     */
    suspend fun createPremiumSubscription(
        customerId: String,
        priceId: String,
        userId: String
    ): Result<String> {
        return try {
            val subscription = stripeAccessor.createSubscription(
                customerId = customerId,
                priceId = priceId,
                metadata = mapOf(
                    "beenthere_user_id" to userId,
                    "subscription_type" to "premium"
                )
            )
            Result.success(subscription.id)
        } catch (e: Exception) {
            Result.failure(Exception("Stripe subscription creation failed: ${e.message}", e))
        }
    }
    
    /**
     * Get subscription status for user
     */
    suspend fun getSubscriptionStatus(subscriptionId: String): Result<SubscriptionStatus> {
        return try {
            val subscription = stripeAccessor.getSubscription(subscriptionId)
            if (subscription != null) {
                val status = when (subscription.status) {
                    "active" -> SubscriptionStatus.ACTIVE
                    "past_due" -> SubscriptionStatus.PAST_DUE
                    "canceled" -> SubscriptionStatus.CANCELED
                    "unpaid" -> SubscriptionStatus.UNPAID
                    else -> SubscriptionStatus.INACTIVE
                }
                Result.success(status)
            } else {
                Result.failure(Exception("Subscription not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Stripe subscription check failed: ${e.message}", e))
        }
    }
    
    /**
     * Cancel user subscription
     */
    suspend fun cancelSubscription(subscriptionId: String): Result<Boolean> {
        return try {
            stripeAccessor.cancelSubscription(subscriptionId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Stripe subscription cancellation failed: ${e.message}", e))
        }
    }
}

/**
 * BeenThere subscription status enum
 */
enum class SubscriptionStatus {
    ACTIVE,
    INACTIVE,
    PAST_DUE,
    CANCELED,
    UNPAID
}