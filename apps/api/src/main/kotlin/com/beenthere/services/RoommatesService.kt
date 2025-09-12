package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.common.PaginatedResponse
import com.beenthere.dto.roommates.*
import com.beenthere.entities.*
import com.beenthere.repositories.*
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class RoommatesService(
    private val userRepository: UserRepository,
    private val roommateSwipeRepository: RoommateSwipeRepository,
    private val roommateMatchRepository: RoommateMatchRepository,
    private val roommateMessageRepository: RoommateMessageRepository
) {
    
    suspend fun getRoommatesFeed(
        userId: UUID,
        cursor: String?,
        limit: Int = 50,
        filters: Map<String, String> = emptyMap()
    ): Result<RoommatesFeedRes> {
        return try {
            // Parse cursor for pagination
            val cursorInstant = cursor?.let { Instant.parse(it) }
            
            // Get users the current user has already swiped on
            val swipedUserIds = roommateSwipeRepository.findByUserId(userId).toList().map { it.targetUserId }
            
            // Get potential roommates (excluding self and already swiped)
            val potentialRoommates = if (cursorInstant != null) {
                userRepository.findPotentialRoommatesAfterCursor(userId, swipedUserIds, cursorInstant, limit)
            } else {
                userRepository.findPotentialRoommates(userId, swipedUserIds, limit)
            }.toList()
            
            val feedItems = potentialRoommates.map { user ->
                RoommateFeedItem(
                    userId = user.id.toString(),
                    displayName = user.displayName,
                    photoUrl = user.photoUrl,
                    bio = user.bio,
                    prefs = null, // TODO: Add preferences from user profile when implemented
                    hasApartment = user.hasApartment
                )
            }
            
            val nextCursor = if (feedItems.size == limit) {
                potentialRoommates.lastOrNull()?.createdAt?.toString()
            } else null
            
            Result.success(RoommatesFeedRes(
                items = feedItems,
                nextCursor = nextCursor
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    @Transactional
    suspend fun swipeOnRoommate(req: SwipeReq, userId: UUID): Result<SwipeRes> {
        return try {
            val targetUserId = UUID.fromString(req.targetUserId)
            
            // Check if target user exists
            val targetUser = userRepository.findById(targetUserId)
                ?: return Result.failure(ServiceError.UserNotFound(req.targetUserId))
            
            // Check if already swiped
            val existingSwipe = roommateSwipeRepository.findByUserIdAndTargetUserId(userId, targetUserId)
            if (existingSwipe != null) {
                return Result.failure(ServiceError.ValidationError("Already swiped on this user", "User has already swiped on this user"))
            }
            
            // Create swipe record
            val swipe = RoommateSwipeEntity(
                userId = userId,
                targetUserId = targetUserId,
                action = req.action.name
            )
            roommateSwipeRepository.save(swipe)
            
            // Check for mutual like to create match
            var matchId: String? = null
            if (req.action == SwipeAction.LIKE) {
                val reciprocalSwipe = roommateSwipeRepository.findByUserIdAndTargetUserId(targetUserId, userId)
                if (reciprocalSwipe?.action == SwipeAction.LIKE.name) {
                    // Create match
                    val match = RoommateMatchEntity(
                        aUserId = minOf(userId, targetUserId),
                        bUserId = maxOf(userId, targetUserId)
                    )
                    val savedMatch = roommateMatchRepository.save(match)
                    matchId = savedMatch.id.toString()
                }
            }
            
            Result.success(SwipeRes(matchId = matchId))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    suspend fun getMatches(userId: UUID): Result<MatchesRes> {
        return try {
            val matches = roommateMatchRepository.findByUserId(userId).toList()
            
            val matchList = matches.map { matchEntity ->
                // Determine the other user ID
                val otherUserId = if (matchEntity.aUserId == userId) matchEntity.bUserId else matchEntity.aUserId
                val otherUser = userRepository.findById(otherUserId)!!
                
                // Get last message
                val lastMessage = roommateMessageRepository.findLastMessageForMatch(matchEntity.id!!).toList().firstOrNull()
                
                Match(
                    id = matchEntity.id.toString(),
                    otherUserId = otherUser.id.toString(),
                    otherUserName = otherUser.displayName,
                    otherUserPhotoUrl = otherUser.photoUrl,
                    createdAt = matchEntity.createdAt.toString(),
                    lastMessageAt = lastMessage?.createdAt?.toString(),
                    lastMessage = lastMessage?.body
                )
            }
            
            Result.success(MatchesRes(matches = matchList))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    suspend fun getMessages(
        matchId: UUID,
        userId: UUID,
        cursor: String?,
        limit: Int = 50
    ): Result<MessagesRes> {
        return try {
            // Verify user is part of this match
            val match = roommateMatchRepository.findById(matchId)
                ?: return Result.failure(ServiceError.ValidationError("Match not found", "Match with ID $matchId not found"))
            
            if (match.aUserId != userId && match.bUserId != userId) {
                return Result.failure(ServiceError.ValidationError("Not authorized for this match", "User not authorized for match"))
            }
            
            // Parse cursor for pagination
            val cursorInstant = cursor?.let { Instant.parse(it) }
            
            val messages = if (cursorInstant != null) {
                roommateMessageRepository.findByMatchIdBeforeCursor(matchId, cursorInstant, limit)
            } else {
                roommateMessageRepository.findByMatchIdOrderByCreatedAtDesc(matchId, limit)
            }.toList()
            
            val messageList = messages.map { messageEntity ->
                Message(
                    id = messageEntity.id.toString(),
                    senderUserId = messageEntity.senderUserId.toString(),
                    body = messageEntity.body,
                    createdAt = messageEntity.createdAt.toString()
                )
            }
            
            val nextCursor = if (messageList.size == limit) {
                messages.lastOrNull()?.createdAt?.toString()
            } else null
            
            Result.success(MessagesRes(
                items = messageList,
                nextCursor = nextCursor
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    @Transactional
    suspend fun sendMessage(
        matchId: UUID,
        userId: UUID,
        req: SendMessageReq
    ): Result<SendMessageRes> {
        return try {
            // Verify user is part of this match
            val match = roommateMatchRepository.findById(matchId)
                ?: return Result.failure(ServiceError.ValidationError("Match not found", "Match with ID $matchId not found"))
            
            if (match.aUserId != userId && match.bUserId != userId) {
                return Result.failure(ServiceError.ValidationError("Not authorized for this match", "User not authorized for match"))
            }
            
            // Create message
            val message = RoommateMessageEntity(
                matchId = matchId,
                senderUserId = userId,
                body = req.body
            )
            val savedMessage = roommateMessageRepository.save(message)
            
            Result.success(SendMessageRes(id = savedMessage.id.toString()))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
}