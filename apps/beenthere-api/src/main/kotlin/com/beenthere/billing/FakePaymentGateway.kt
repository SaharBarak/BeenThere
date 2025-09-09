package com.beenthere.billing

import com.beenthere.common.AppError
import com.beenthere.common.ErrorFactory
import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@Service
class FakePaymentGateway : PaymentGateway {
    private val payments = mutableMapOf<String, PaymentResponse>()
    private val refunds = mutableMapOf<String, RefundResponse>()

    override fun processPayment(request: PaymentRequest): Mono<Result<PaymentResponse, AppError>> {
        return Mono.fromCallable {
            // Simulate payment processing
            val paymentId = UUID.randomUUID().toString()
            val transactionId = "txn_${UUID.randomUUID().toString().substring(0, 8)}"

            // Simulate different outcomes based on amount
            val status =
                when {
                    request.amount <= BigDecimal.ZERO -> PaymentStatus.FAILED
                    request.amount > BigDecimal("10000") -> PaymentStatus.FAILED // Simulate high amount failure
                    else -> PaymentStatus.COMPLETED
                }

            val response =
                PaymentResponse(
                    paymentId = paymentId,
                    status = status,
                    amount = request.amount,
                    currency = request.currency,
                    transactionId = transactionId,
                )

            payments[paymentId] = response
            Ok(response) as Result<PaymentResponse, AppError>
        }
    }

    override fun refundPayment(
        paymentId: String,
        amount: BigDecimal,
    ): Mono<Result<RefundResponse, AppError>> {
        return Mono.fromCallable {
            val payment = payments[paymentId]
            if (payment == null) {
                Err(ErrorFactory.notFoundError("Payment not found"))
            } else if (payment.status != PaymentStatus.COMPLETED) {
                Err(ErrorFactory.validationError("Cannot refund payment that is not completed"))
            } else if (amount > payment.amount) {
                Err(ErrorFactory.validationError("Refund amount cannot exceed payment amount"))
            } else {
                val refundId = UUID.randomUUID().toString()
                val refund =
                    RefundResponse(
                        refundId = refundId,
                        paymentId = paymentId,
                        amount = amount,
                        status = RefundStatus.COMPLETED,
                    )

                refunds[refundId] = refund

                // Update payment status
                payments[paymentId] = payment.copy(status = PaymentStatus.REFUNDED)

                Ok(refund) as Result<RefundResponse, AppError>
            }
        }
    }

    override fun getPaymentStatus(paymentId: String): Mono<Result<PaymentStatus, AppError>> {
        return Mono.fromCallable {
            val payment = payments[paymentId]
            if (payment != null) {
                Ok(payment.status) as Result<PaymentStatus, AppError>
            } else {
                Err(ErrorFactory.notFoundError("Payment not found"))
            }
        }
    }
}
