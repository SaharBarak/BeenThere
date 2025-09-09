package com.beenthere.billing

import com.beenthere.error.ServiceError
import com.beenthere.subscription.SubscriptionService
import com.michaelbull.result.Result
import com.michaelbull.result.Ok
import com.michaelbull.result.Err
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class BillingService(
    private val subscriptionService: SubscriptionService
) {

    fun createCheckout(request: CreateCheckoutRequest): Mono<Result<CheckoutResponse, ServiceError>> {
        val userId = "current-user-id" // This would come from security context
        
        return try {
            // Create checkout session with payment provider (simplified for now)
            val response = CheckoutResponse(
                checkoutUrl = "https://checkout.example.com/session_123",
                providerRef = "provider_ref_123"
            )
            
            Mono.just(Ok(response) as Result<CheckoutResponse, ServiceError>)
        } catch (e: Exception) {
            Mono.just(Err(ServiceError.InternalServerError("Failed to create checkout: ${e.message}")))
        }
    }

    fun getBillingStatus(): Mono<Result<BillingStatusResponse, ServiceError>> {
        val userId = "current-user-id" // This would come from security context
        
        return subscriptionService.getSubscriptionStatus(userId)
            .map { result ->
                result.fold(
                    success = { subscription ->
                        Ok(BillingStatusResponse(
                            status = subscription.status,
                            periodEnd = subscription.periodEnd
                        )) as Result<BillingStatusResponse, ServiceError>
                    },
                    failure = { error ->
                        Err(error)
                    }
                )
            }
    }

    fun handleWebhook(webhookData: Map<String, Any>): Mono<Result<Unit, ServiceError>> {
        return try {
            // Process webhook from payment provider
            val eventType = webhookData["type"] as? String
            val userId = webhookData["user_id"] as? String
            
            when (eventType) {
                "payment.succeeded" -> {
                    // Activate subscription
                    subscriptionService.createSubscription(userId ?: "", "monthly")
                        .map { subscriptionResult ->
                            subscriptionResult.fold(
                                success = { Ok(Unit) as Result<Unit, ServiceError> },
                                failure = { Err(it) }
                            )
                        }
                }
                "payment.failed" -> {
                    // Handle failed payment
                    Mono.just(Ok(Unit) as Result<Unit, ServiceError>)
                }
                else -> {
                    Mono.just(Ok(Unit) as Result<Unit, ServiceError>)
                }
            }
        } catch (e: Exception) {
            Mono.just(Err(ServiceError.InternalServerError("Failed to process webhook: ${e.message}")))
        }
    }

    private fun getAmountForPlan(plan: String, currency: String): Int {
        return when (plan) {
            "monthly" -> when (currency) {
                "ILS" -> 20 // 20 ILS per month
                "USD" -> 5  // 5 USD per month
                else -> 20
            }
            else -> 20
        }
    }
}

data class CreateCheckoutRequest(
    val plan: String, // "monthly"
    val currency: String // "ILS"
)

data class CheckoutResponse(
    val checkoutUrl: String,
    val providerRef: String
)

data class BillingStatusResponse(
    val status: String,
    val periodEnd: java.time.Instant? = null
)

