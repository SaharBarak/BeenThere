import { z } from 'zod';
import { UuidSchema } from './common.js';
/**
 * Google authentication request
 */
export const GoogleAuthReqSchema = z.object({
    idToken: z.string().min(1, "ID token is required"),
});
/**
 * User info returned from authentication
 */
export const UserInfoSchema = z.object({
    id: UuidSchema,
    email: z.string().email(),
    displayName: z.string().min(1).max(100),
    photoUrl: z.string().url().optional(),
    bio: z.string().max(300).optional(),
    hasApartment: z.boolean().default(false),
    openToMatching: z.boolean().default(true),
    createdAt: z.string().datetime(),
});
/**
 * Google authentication response
 */
export const GoogleAuthResSchema = z.object({
    jwt: z.string().min(1, "JWT token is required"),
    user: UserInfoSchema,
});
/**
 * User profile for display (public info)
 */
export const UserProfileSchema = z.object({
    user: z.object({
        id: UuidSchema,
        displayName: z.string().min(1).max(100),
        photoUrl: z.string().url().optional(),
        bio: z.string().max(300).optional(),
    }),
    ratingsSummary: z.object({
        roommateAvg: z.number().min(1).max(10).optional(),
        count: z.number().int().nonnegative(),
    }),
});
/**
 * Update profile response
 */
export const UpdateProfileResSchema = z.object({
    success: z.boolean(),
});
/**
 * Update profile request
 */
export const UpdateProfileReqSchema = z.object({
    displayName: z.string().min(1, "Display name is required").max(100, "Display name too long").optional(),
    bio: z.string().max(300, "Bio too long").optional(),
    hasApartment: z.boolean().optional(),
    openToMatching: z.boolean().optional(),
});
