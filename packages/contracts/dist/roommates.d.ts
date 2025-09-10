import { z } from 'zod';
/**
 * Roommate feed item
 */
export declare const RoommateFeedItemSchema: z.ZodObject<{
    userId: z.ZodString;
    displayName: z.ZodString;
    photoUrl: z.ZodOptional<z.ZodString>;
    bio: z.ZodOptional<z.ZodString>;
    prefs: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodUnknown>>;
    hasApartment: z.ZodBoolean;
}, "strip", z.ZodTypeAny, {
    userId: string;
    displayName: string;
    hasApartment: boolean;
    photoUrl?: string | undefined;
    bio?: string | undefined;
    prefs?: Record<string, unknown> | undefined;
}, {
    userId: string;
    displayName: string;
    hasApartment: boolean;
    photoUrl?: string | undefined;
    bio?: string | undefined;
    prefs?: Record<string, unknown> | undefined;
}>;
export type RoommateFeedItem = z.infer<typeof RoommateFeedItemSchema>;
/**
 * Roommates feed response
 */
export declare const RoommatesFeedResSchema: z.ZodObject<{
    items: z.ZodArray<z.ZodObject<{
        userId: z.ZodString;
        displayName: z.ZodString;
        photoUrl: z.ZodOptional<z.ZodString>;
        bio: z.ZodOptional<z.ZodString>;
        prefs: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodUnknown>>;
        hasApartment: z.ZodBoolean;
    }, "strip", z.ZodTypeAny, {
        userId: string;
        displayName: string;
        hasApartment: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        prefs?: Record<string, unknown> | undefined;
    }, {
        userId: string;
        displayName: string;
        hasApartment: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        prefs?: Record<string, unknown> | undefined;
    }>, "many">;
    nextCursor: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    items: {
        userId: string;
        displayName: string;
        hasApartment: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        prefs?: Record<string, unknown> | undefined;
    }[];
    nextCursor?: string | undefined;
}, {
    items: {
        userId: string;
        displayName: string;
        hasApartment: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        prefs?: Record<string, unknown> | undefined;
    }[];
    nextCursor?: string | undefined;
}>;
export type RoommatesFeedRes = z.infer<typeof RoommatesFeedResSchema>;
/**
 * Swipe action enum
 */
export declare const SwipeActionSchema: z.ZodEnum<["LIKE", "PASS"]>;
export type SwipeAction = z.infer<typeof SwipeActionSchema>;
/**
 * Swipe request (roommates)
 */
export declare const SwipeReqSchema: z.ZodObject<{
    targetUserId: z.ZodString;
    action: z.ZodEnum<["LIKE", "PASS"]>;
}, "strip", z.ZodTypeAny, {
    targetUserId: string;
    action: "LIKE" | "PASS";
}, {
    targetUserId: string;
    action: "LIKE" | "PASS";
}>;
export type SwipeReq = z.infer<typeof SwipeReqSchema>;
/**
 * Swipe response - returns matchId if mutual match
 */
export declare const SwipeResSchema: z.ZodObject<{
    matchId: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    matchId?: string | undefined;
}, {
    matchId?: string | undefined;
}>;
export type SwipeRes = z.infer<typeof SwipeResSchema>;
/**
 * Match item
 */
export declare const MatchSchema: z.ZodObject<{
    id: z.ZodString;
    otherUserId: z.ZodString;
    otherUserName: z.ZodString;
    otherUserPhotoUrl: z.ZodOptional<z.ZodString>;
    createdAt: z.ZodString;
    lastMessageAt: z.ZodOptional<z.ZodString>;
    lastMessage: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    id: string;
    otherUserId: string;
    otherUserName: string;
    createdAt: string;
    otherUserPhotoUrl?: string | undefined;
    lastMessageAt?: string | undefined;
    lastMessage?: string | undefined;
}, {
    id: string;
    otherUserId: string;
    otherUserName: string;
    createdAt: string;
    otherUserPhotoUrl?: string | undefined;
    lastMessageAt?: string | undefined;
    lastMessage?: string | undefined;
}>;
export type Match = z.infer<typeof MatchSchema>;
/**
 * Matches list response
 */
export declare const MatchesResSchema: z.ZodObject<{
    matches: z.ZodArray<z.ZodObject<{
        id: z.ZodString;
        otherUserId: z.ZodString;
        otherUserName: z.ZodString;
        otherUserPhotoUrl: z.ZodOptional<z.ZodString>;
        createdAt: z.ZodString;
        lastMessageAt: z.ZodOptional<z.ZodString>;
        lastMessage: z.ZodOptional<z.ZodString>;
    }, "strip", z.ZodTypeAny, {
        id: string;
        otherUserId: string;
        otherUserName: string;
        createdAt: string;
        otherUserPhotoUrl?: string | undefined;
        lastMessageAt?: string | undefined;
        lastMessage?: string | undefined;
    }, {
        id: string;
        otherUserId: string;
        otherUserName: string;
        createdAt: string;
        otherUserPhotoUrl?: string | undefined;
        lastMessageAt?: string | undefined;
        lastMessage?: string | undefined;
    }>, "many">;
}, "strip", z.ZodTypeAny, {
    matches: {
        id: string;
        otherUserId: string;
        otherUserName: string;
        createdAt: string;
        otherUserPhotoUrl?: string | undefined;
        lastMessageAt?: string | undefined;
        lastMessage?: string | undefined;
    }[];
}, {
    matches: {
        id: string;
        otherUserId: string;
        otherUserName: string;
        createdAt: string;
        otherUserPhotoUrl?: string | undefined;
        lastMessageAt?: string | undefined;
        lastMessage?: string | undefined;
    }[];
}>;
export type MatchesRes = z.infer<typeof MatchesResSchema>;
/**
 * Chat message
 */
export declare const MessageSchema: z.ZodObject<{
    id: z.ZodString;
    senderUserId: z.ZodString;
    body: z.ZodString;
    createdAt: z.ZodString;
}, "strip", z.ZodTypeAny, {
    id: string;
    createdAt: string;
    senderUserId: string;
    body: string;
}, {
    id: string;
    createdAt: string;
    senderUserId: string;
    body: string;
}>;
export type Message = z.infer<typeof MessageSchema>;
/**
 * Messages list response (paginated)
 */
export declare const MessagesResSchema: z.ZodObject<{
    items: z.ZodArray<z.ZodObject<{
        id: z.ZodString;
        senderUserId: z.ZodString;
        body: z.ZodString;
        createdAt: z.ZodString;
    }, "strip", z.ZodTypeAny, {
        id: string;
        createdAt: string;
        senderUserId: string;
        body: string;
    }, {
        id: string;
        createdAt: string;
        senderUserId: string;
        body: string;
    }>, "many">;
    nextCursor: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    items: {
        id: string;
        createdAt: string;
        senderUserId: string;
        body: string;
    }[];
    nextCursor?: string | undefined;
}, {
    items: {
        id: string;
        createdAt: string;
        senderUserId: string;
        body: string;
    }[];
    nextCursor?: string | undefined;
}>;
export type MessagesRes = z.infer<typeof MessagesResSchema>;
/**
 * Send message request
 */
export declare const SendMessageReqSchema: z.ZodObject<{
    body: z.ZodString;
}, "strip", z.ZodTypeAny, {
    body: string;
}, {
    body: string;
}>;
export type SendMessageReq = z.infer<typeof SendMessageReqSchema>;
/**
 * Send message response
 */
export declare const SendMessageResSchema: z.ZodObject<{
    id: z.ZodString;
}, "strip", z.ZodTypeAny, {
    id: string;
}, {
    id: string;
}>;
export type SendMessageRes = z.infer<typeof SendMessageResSchema>;
