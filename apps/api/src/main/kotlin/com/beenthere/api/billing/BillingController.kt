package com.beenthere.api.billing

import com.beenthere.common.toResponseEntity
import com.github.michaelbull.result.Ok
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Billing endpoints for subscription status and webhooks.
 * MVP: Feature flag stubs for subscription gating.
 */
@RestController
@RequestMapping("/api/v1/billing")
class BillingController {
    
    /**
     * Get user's subscription status.
     * Returns current subscription state for feature gating.
     */
    @GetMapping("/status")
    fun getBillingStatus(): ResponseEntity<*> {
        // TODO: Implement subscription status lookup
        // TODO: Get current user from authentication context
        // TODO: Return subscription details from subscriptions table
        
        val mockResponse = mapOf(
            "status" to "ACTIVE", // NONE, ACTIVE, EXPIRED
            "periodEnd" to "2025-01-01T00:00:00Z"
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Webhook endpoint for payment provider.
     * Updates subscription status based on payment events.
     */
    @PostMapping("/webhook")
    fun handleBillingWebhook(@RequestBody request: Map<String, Any>): ResponseEntity<*> {
        // TODO: Implement webhook validation and processing
        // TODO: Verify webhook signature from payment provider
        // TODO: Update subscription status based on action (activate/expire)
        // TODO: Handle subscription period updates
        
        val mockResponse = mapOf(
            "received" to true
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
}