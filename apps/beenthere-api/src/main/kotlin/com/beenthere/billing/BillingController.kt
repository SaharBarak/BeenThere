package com.beenthere.billing

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/billing")
class BillingController(
    private val billingService: BillingService
) {

    @PostMapping("/checkout")
    fun createCheckout(@Valid @RequestBody request: CreateCheckoutRequest): Mono<ResponseEntity<ApiResponse<CheckoutResponse>>> {
        return billingService.createCheckout(request)
            .map { result ->
                result.fold(
                    success = { checkout ->
                        ApiResponse.success(checkout).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<CheckoutResponse>().toResponseEntity()
                    }
                )
            }
    }

    @GetMapping("/status")
    fun getBillingStatus(): Mono<ResponseEntity<ApiResponse<BillingStatusResponse>>> {
        return billingService.getBillingStatus()
            .map { result ->
                result.fold(
                    success = { status ->
                        ApiResponse.success(status).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<BillingStatusResponse>().toResponseEntity()
                    }
                )
            }
    }

    @PostMapping("/webhook")
    fun handleWebhook(@RequestBody webhookData: Map<String, Any>): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return billingService.handleWebhook(webhookData)
            .map { result ->
                result.fold(
                    success = {
                        ApiResponse.success(Unit).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<Unit>().toResponseEntity()
                    }
                )
            }
    }
}
