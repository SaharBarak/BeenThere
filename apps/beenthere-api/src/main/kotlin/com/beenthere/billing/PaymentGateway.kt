package com.beenthere.billing

import com.beenthere.common.AppError
import com.michaelbull.result.Result
import reactor.core.publisher.Mono

interface PaymentGateway {
    fun processPayment(request: PaymentRequest): Mono<Result<PaymentResponse, AppError>>

    fun refundPayment(
        paymentId: String,
        amount: java.math.BigDecimal,
    ): Mono<Result<RefundResponse, AppError>>

    fun getPaymentStatus(paymentId: String): Mono<Result<PaymentStatus, AppError>>
}

data class PaymentRequest(
    val amount: java.math.BigDecimal,
    val currency: String = "USD",
    val description: String,
    val customerId: String,
    val metadata: Map<String, String> = emptyMap(),
)

data class PaymentResponse(
    val paymentId: String,
    val status: PaymentStatus,
    val amount: java.math.BigDecimal,
    val currency: String,
    val transactionId: String? = null,
)

data class RefundResponse(
    val refundId: String,
    val paymentId: String,
    val amount: java.math.BigDecimal,
    val status: RefundStatus,
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
}

enum class RefundStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
}
