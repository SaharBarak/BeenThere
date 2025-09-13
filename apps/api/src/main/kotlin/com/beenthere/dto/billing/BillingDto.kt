package com.beenthere.dto.billing

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Billing DTOs for subscription feature flags (MVP stubs)
 */

data class BillingStatus(
    @JsonProperty("isSubscribed")
    val isSubscribed: Boolean,
    
    @JsonProperty("subscriptionTier")
    val subscriptionTier: String?,
    
    @JsonProperty("expiresAt")
    val expiresAt: String?
)

data class WebhookResponse(
    @JsonProperty("received")
    val received: Boolean
)