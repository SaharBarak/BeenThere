package com.beenthere.util

import com.github.michaelbull.result.*
import com.beenthere.common.ServiceError
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Phone number utilities for E.164 normalization and HMAC hashing.
 * Ensures PII protection by never storing raw phone numbers.
 */
@Component
class PhoneUtils(
    @Value("\${beenthere.security.phone-hash-secret}")
    private val phoneHashSecret: String
) {
    
    /**
     * Normalize phone number to E.164 format.
     * Supports Israeli phone numbers and international format.
     */
    fun normalizeToE164(phone: String): Result<String, ServiceError.ValidationError> {
        val cleaned = phone.replace(Regex("[^+\\d]"), "")
        
        return when {
            // Already in E.164 format
            cleaned.startsWith("+") && cleaned.length >= 10 -> {
                if (isValidE164(cleaned)) {
                    Ok(cleaned)
                } else {
                    Err(ServiceError.ValidationError("phone", "Invalid E.164 format"))
                }
            }
            
            // Israeli numbers starting with 0
            cleaned.startsWith("0") && cleaned.length == 10 -> {
                val withoutLeadingZero = cleaned.substring(1)
                val e164 = "+972$withoutLeadingZero"
                if (isValidIsraeliNumber(withoutLeadingZero)) {
                    Ok(e164)
                } else {
                    Err(ServiceError.ValidationError("phone", "Invalid Israeli phone number format"))
                }
            }
            
            // Israeli numbers without country code or leading zero
            cleaned.length == 9 && (cleaned.startsWith("5") || cleaned.startsWith("2") || cleaned.startsWith("3")) -> {
                val e164 = "+972$cleaned"
                Ok(e164)
            }
            
            // Israeli numbers starting with 972 without +
            cleaned.startsWith("972") && cleaned.length == 12 -> {
                Ok("+$cleaned")
            }
            
            else -> {
                Err(ServiceError.ValidationError("phone", "Unsupported phone number format. Please use Israeli format or international E.164."))
            }
        }
    }
    
    /**
     * Generate HMAC-SHA256 hash of E.164 phone number.
     * This is what gets stored in the database.
     */
    fun hashPhone(e164Phone: String): Result<String, ServiceError.InternalError> = try {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(phoneHashSecret.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        
        val hashBytes = mac.doFinal(e164Phone.toByteArray())
        val hexString = hashBytes.joinToString("") { "%02x".format(it) }
        
        Ok(hexString)
    } catch (e: Exception) {
        Err(ServiceError.InternalError("Failed to hash phone number: ${e.message}"))
    }
    
    /**
     * Normalize and hash phone number in one operation.
     */
    fun normalizeAndHash(phone: String): Result<String, ServiceError> {
        return normalizeToE164(phone)
            .andThen { e164 -> hashPhone(e164) }
    }
    
    private fun isValidE164(phone: String): Boolean {
        return phone.matches(Regex("^\\+[1-9]\\d{1,14}$"))
    }
    
    private fun isValidIsraeliNumber(number: String): Boolean {
        return when {
            // Mobile numbers: 50, 51, 52, 53, 54, 55, 56, 57, 58, 59
            number.startsWith("5") -> number.length == 9 && number.matches(Regex("^5[0-9]\\d{7}$"))
            // Landline numbers: 02, 03, 04, 08, 09
            number.startsWith("2") || number.startsWith("3") || number.startsWith("4") || 
            number.startsWith("8") || number.startsWith("9") -> {
                number.length == 9 && number.matches(Regex("^[2-9]\\d{8}$"))
            }
            else -> false
        }
    }
}