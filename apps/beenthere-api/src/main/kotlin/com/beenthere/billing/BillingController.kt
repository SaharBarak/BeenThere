package com.beenthere.billing

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/billing")
class BillingController(
    private val billingService: BillingService,
) {
    @PostMapping("/checkout")
    fun createCheckout(
        @Valid @RequestBody request: CreateCheckoutRequest,
    ): Mono<ResponseEntity<ApiResponse<CheckoutResponse>>> {
        return billingService.createCheckout(request)
            .map { result ->
                result.fold(
                    success = { checkout: CheckoutResponse ->
                        ApiResponse.success(checkout).toResponseEntity()
                    },
                    failure = { error: ServiceError ->
                        error.toApiResponse<CheckoutResponse>().toResponseEntity()
                    },
                )
            }
    }

    @GetMapping("/status")
    fun getBillingStatus(): Mono<ResponseEntity<ApiResponse<BillingStatusResponse>>> {
        return billingService.getBillingStatus()
            .map { result ->
                result.fold(
                    success = { status: BillingStatusResponse ->
                        ApiResponse.success(status).toResponseEntity()
                    },
                    failure = { error: ServiceError ->
                        error.toApiResponse<BillingStatusResponse>().toResponseEntity()
                    },
                )
            }
    }

    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody webhookData: Map<String, Any>,
    ): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return billingService.handleWebhook(webhookData)
            .map { result ->
                result.fold(
                    success = { _: Unit ->
                        ApiResponse.success(Unit).toResponseEntity()
                    },
                    failure = { error: ServiceError ->
                        error.toApiResponse<Unit>().toResponseEntity()
                    },
                )
            }
    }
}
