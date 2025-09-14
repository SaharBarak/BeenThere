package com.beenthere.controllers

import com.beenthere.dto.billing.BillingStatus
import com.beenthere.dto.billing.WebhookResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/billing")
class BillingController {
    
    @GetMapping("/status")
    suspend fun getBillingStatus(
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<BillingStatus> {
        // MVP: Return stub data - no live billing
        return ResponseEntity.ok(BillingStatus(
            isSubscribed = false,
            subscriptionTier = null,
            expiresAt = null
        ))
    }
    
    @PostMapping("/webhook")
    suspend fun handleWebhook(
        @RequestBody payload: Map<String, Any>
    ): ResponseEntity<WebhookResponse> {
        // MVP: Just acknowledge webhook - no processing
        return ResponseEntity.ok(WebhookResponse(received = true))
    }
}