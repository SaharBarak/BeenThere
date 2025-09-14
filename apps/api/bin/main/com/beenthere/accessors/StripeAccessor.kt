package com.beenthere.accessors

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import com.stripe.model.Subscription
import com.stripe.param.CustomerCreateParams
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.SubscriptionCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Generic Stripe Accessor for all Stripe operations
 * Provides async operations for customer and billing management
 */
@Component
class StripeAccessor(
    @Value("\${beenthere.stripe.secret-key:}")
    private val stripeSecretKey: String
) {
    
    @PostConstruct
    fun init() {
        Stripe.apiKey = stripeSecretKey
    }
    
    /**
     * Create a new Stripe customer
     */
    suspend fun createCustomer(
        email: String,
        name: String,
        metadata: Map<String, String> = emptyMap()
    ): Customer = withContext(Dispatchers.IO) {
        val params = CustomerCreateParams.builder()
            .setEmail(email)
            .setName(name)
            .putAllMetadata(metadata)
            .build()
        
        Customer.create(params)
    }
    
    /**
     * Retrieve a Stripe customer by ID
     */
    suspend fun getCustomer(customerId: String): Customer? = withContext(Dispatchers.IO) {
        try {
            Customer.retrieve(customerId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create a payment intent for subscription
     */
    suspend fun createPaymentIntent(
        amount: Long, // Amount in agorot (Israeli currency cents)
        currency: String = "ils",
        customerId: String,
        metadata: Map<String, String> = emptyMap()
    ): PaymentIntent = withContext(Dispatchers.IO) {
        val params = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency(currency)
            .setCustomer(customerId)
            .putAllMetadata(metadata)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build()
        
        PaymentIntent.create(params)
    }
    
    /**
     * Create a subscription for a customer
     */
    suspend fun createSubscription(
        customerId: String,
        priceId: String,
        metadata: Map<String, String> = emptyMap()
    ): Subscription = withContext(Dispatchers.IO) {
        val params = SubscriptionCreateParams.builder()
            .setCustomer(customerId)
            .addItem(
                SubscriptionCreateParams.Item.builder()
                    .setPrice(priceId)
                    .build()
            )
            .putAllMetadata(metadata)
            .build()
        
        Subscription.create(params)
    }
    
    /**
     * Retrieve a subscription by ID
     */
    suspend fun getSubscription(subscriptionId: String): Subscription? = withContext(Dispatchers.IO) {
        try {
            Subscription.retrieve(subscriptionId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Cancel a subscription
     */
    suspend fun cancelSubscription(subscriptionId: String): Subscription = withContext(Dispatchers.IO) {
        val subscription = Subscription.retrieve(subscriptionId)
        subscription.cancel()
    }
}