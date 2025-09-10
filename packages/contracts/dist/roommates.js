import { z } from 'zod';
import { UuidSchema, PaginatedResponseSchema } from './common.js';
/**
 * Roommate feed item
 */
export const RoommateFeedItemSchema = z.object({
    userId: UuidSchema,
    displayName: z.string(),
    photoUrl: z.string().url().optional(),
    bio: z.string().optional(),
    prefs: z.record(z.string(), z.unknown()).optional(),
    hasApartment: z.boolean(),
});
/**
 * Roommates feed response
 */
export const RoommatesFeedResSchema = PaginatedResponseSchema(RoommateFeedItemSchema);
/**
 * Swipe action enum
 */
export const SwipeActionSchema = z.enum(['LIKE', 'PASS']);
/**
 * Swipe request (roommates)
 */
export const SwipeReqSchema = z.object({
    targetUserId: UuidSchema,
    action: SwipeActionSchema,
});
/**
 * Swipe response - returns matchId if mutual match
 */
export const SwipeResSchema = z.object({
    matchId: UuidSchema.optional(),
});
/**
 * Match item
 */
export const MatchSchema = z.object({
    id: UuidSchema,
    otherUserId: UuidSchema,
    otherUserName: z.string(),
    otherUserPhotoUrl: z.string().url().optional(),
    createdAt: z.string().datetime(),
    lastMessageAt: z.string().datetime().optional(),
    lastMessage: z.string().optional(),
});
/**
 * Matches list response
 */
export const MatchesResSchema = z.object({
    matches: z.array(MatchSchema),
});
/**
 * Chat message
 */
export const MessageSchema = z.object({
    id: UuidSchema,
    senderUserId: UuidSchema,
    body: z.string().min(1, "Message body cannot be empty"),
    createdAt: z.string().datetime(),
});
/**
 * Messages list response (paginated)
 */
export const MessagesResSchema = PaginatedResponseSchema(MessageSchema);
/**
 * Send message request
 */
export const SendMessageReqSchema = z.object({
    body: z.string().min(1, "Message body cannot be empty").max(1000, "Message too long"),
});
/**
 * Send message response
 */
export const SendMessageResSchema = z.object({
    id: UuidSchema,
});
