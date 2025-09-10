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
export type RoommateFeedItem = z.infer<typeof RoommateFeedItemSchema>;

/**
 * Roommates feed response
 */
export const RoommatesFeedResSchema = PaginatedResponseSchema(RoommateFeedItemSchema);
export type RoommatesFeedRes = z.infer<typeof RoommatesFeedResSchema>;

/**
 * Swipe action enum
 */
export const SwipeActionSchema = z.enum(['LIKE', 'PASS']);
export type SwipeAction = z.infer<typeof SwipeActionSchema>;

/**
 * Swipe request (roommates)
 */
export const SwipeReqSchema = z.object({
  targetUserId: UuidSchema,
  action: SwipeActionSchema,
});
export type SwipeReq = z.infer<typeof SwipeReqSchema>;

/**
 * Swipe response - returns matchId if mutual match
 */
export const SwipeResSchema = z.object({
  matchId: UuidSchema.optional(),
});
export type SwipeRes = z.infer<typeof SwipeResSchema>;

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
export type Match = z.infer<typeof MatchSchema>;

/**
 * Matches list response
 */
export const MatchesResSchema = z.object({
  matches: z.array(MatchSchema),
});
export type MatchesRes = z.infer<typeof MatchesResSchema>;

/**
 * Chat message
 */
export const MessageSchema = z.object({
  id: UuidSchema,
  senderUserId: UuidSchema,
  body: z.string().min(1, "Message body cannot be empty"),
  createdAt: z.string().datetime(),
});
export type Message = z.infer<typeof MessageSchema>;

/**
 * Messages list response (paginated)
 */
export const MessagesResSchema = PaginatedResponseSchema(MessageSchema);
export type MessagesRes = z.infer<typeof MessagesResSchema>;

/**
 * Send message request
 */
export const SendMessageReqSchema = z.object({
  body: z.string().min(1, "Message body cannot be empty").max(1000, "Message too long"),
});
export type SendMessageReq = z.infer<typeof SendMessageReqSchema>;

/**
 * Send message response
 */
export const SendMessageResSchema = z.object({
  id: UuidSchema,
});
export type SendMessageRes = z.infer<typeof SendMessageResSchema>;