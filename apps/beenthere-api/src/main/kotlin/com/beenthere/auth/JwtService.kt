package com.beenthere.auth

import com.beenthere.error.ServiceError
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret:beenthere-secret-key-that-should-be-changed-in-production}")
    private val secret: String,
    @Value("\${jwt.access-token-expiration:900000}") // 15 minutes
    private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration:604800000}") // 7 days
    private val refreshTokenExpiration: Long
) {
    
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateTokens(userId: String, email: String): TokenPair {
        val now = Date()
        val accessTokenExpiry = Date(now.time + accessTokenExpiration)
        val refreshTokenExpiry = Date(now.time + refreshTokenExpiration)

        val accessToken = Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("type", "access")
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiry)
            .signWith(key)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("type", "refresh")
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiry)
            .signWith(key)
            .compact()

        return TokenPair(accessToken, refreshToken)
    }

    fun generateToken(userId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpiration)

        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("type", "access")
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): String {
        return try {
            val claims = extractAllClaims(token)
            val userId = claims.subject
            val tokenType = claims["type"] as? String
            
            if (userId != null && tokenType == "access" && !isTokenExpired(claims)) {
                userId
            } else {
                throw RuntimeException("Invalid or expired access token")
            }
        } catch (e: Exception) {
            throw RuntimeException("Invalid access token: ${e.message}")
        }
    }

    fun validateRefreshToken(token: String): String {
        return try {
            val claims = extractAllClaims(token)
            val userId = claims.subject
            val tokenType = claims["type"] as? String
            
            if (userId != null && tokenType == "refresh" && !isTokenExpired(claims)) {
                userId
            } else {
                throw RuntimeException("Invalid or expired refresh token")
            }
        } catch (e: Exception) {
            throw RuntimeException("Invalid refresh token: ${e.message}")
        }
    }

    fun extractUserId(token: String): String {
        return try {
            val claims = extractAllClaims(token)
            val userId = claims.subject
            if (userId != null) {
                userId
            } else {
                throw RuntimeException("Token does not contain user ID")
            }
        } catch (e: Exception) {
            throw RuntimeException("Invalid token: ${e.message}")
        }
    }

    fun extractEmail(token: String): String {
        return try {
            val claims = extractAllClaims(token)
            val email = claims["email"] as? String
            if (email != null) {
                email
            } else {
                throw RuntimeException("Token does not contain email")
            }
        } catch (e: Exception) {
            throw RuntimeException("Invalid token: ${e.message}")
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    fun authenticationManager(): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManager { authentication ->
            val token = authentication.credentials as? String
            if (token != null) {
                try {
                    val userId = validateToken(token)
                    val auth = UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        listOf(SimpleGrantedAuthority("ROLE_USER"))
                    )
                    Mono.just(auth)
                } catch (e: Exception) {
                    Mono.error(org.springframework.security.core.AuthenticationException("Invalid token") {})
                }
            } else {
                Mono.error(org.springframework.security.core.AuthenticationException("No token provided") {})
            }
        }
    }
}

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
