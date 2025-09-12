package com.beenthere.util

import com.beenthere.common.ServiceError
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Utilities for phone number handling, normalization, and hashing
 * Following CLAUDE.md requirements for PII protection: never store raw phone numbers
 * Store only HMAC-SHA256(E.164, secret)
 */
@Component
class PhoneUtils(
    @Value("\${beenthere.phone.hash-secret}")
    private val phoneHashSecret: String
) {

    /**
     * Normalize phone number to E.164 format
     * E.164 format: +[country code][area code][local number]
     */
    fun normalizeToE164(phone: String, defaultCountryCode: String = "+972"): Result<String> {
        return try {
            val cleaned = phone.replace(Regex("[^+\\d]"), "")
            
            when {
                cleaned.isEmpty() -> Result.failure(ServiceError.PhoneValidationError(phone))
                cleaned.startsWith("+") -> {
                    if (cleaned.length < 8 || cleaned.length > 15) {
                        Result.failure(ServiceError.PhoneValidationError(phone))
                    } else {
                        Result.success(cleaned)
                    }
                }
                cleaned.startsWith("0") -> {
                    // Israeli format: remove leading 0 and add +972
                    val withoutLeadingZero = cleaned.substring(1)
                    if (withoutLeadingZero.length == 8 || withoutLeadingZero.length == 9) {
                        Result.success("$defaultCountryCode$withoutLeadingZero")
                    } else {
                        Result.failure(ServiceError.PhoneValidationError(phone))
                    }
                }
                else -> {
                    // Assume local number, add default country code
                    if (cleaned.length == 8 || cleaned.length == 9) {
                        Result.success("$defaultCountryCode$cleaned")
                    } else {
                        Result.failure(ServiceError.PhoneValidationError(phone))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(ServiceError.PhoneValidationError(phone))
        }
    }

    /**
     * Hash phone number using HMAC-SHA256
     * This is the only form stored in the database for PII protection
     */
    fun hashPhone(normalizedPhone: String): Result<String> {
        return try {
            val algorithm = "HmacSHA256"
            val secretKeySpec = SecretKeySpec(phoneHashSecret.toByteArray(StandardCharsets.UTF_8), algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(secretKeySpec)
            
            val hashBytes = mac.doFinal(normalizedPhone.toByteArray(StandardCharsets.UTF_8))
            val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
            Result.success(hashHex)
        } catch (e: Exception) {
            Result.failure(ServiceError.PhoneValidationError("Failed to hash phone"))
        }
    }

    /**
     * Process phone number for storage: normalize and hash in one step
     * This is the main method services should use
     */
    fun processPhoneForStorage(rawPhone: String, defaultCountryCode: String = "+972"): Result<String> {
        return normalizeToE164(rawPhone, defaultCountryCode).fold(
            onSuccess = { e164 -> hashPhone(e164) },
            onFailure = { Result.failure(it) }
        )
    }
    
    /**
     * Validate if a string looks like a phone number
     */
    fun isValidPhoneFormat(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.replace(Regex("[^+\\d]"), "")
        return when {
            cleaned.startsWith("+") && cleaned.length >= 8 && cleaned.length <= 15 -> true
            cleaned.startsWith("0") && (cleaned.length == 9 || cleaned.length == 10) -> true
            cleaned.length == 8 || cleaned.length == 9 -> true
            else -> false
        }
    }
}