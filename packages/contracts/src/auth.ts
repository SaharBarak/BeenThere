import { z } from 'zod';
import { UuidSchema } from './common.js';

/**
 * Google authentication request
 */
export const GoogleAuthReqSchema = z.object({
  idToken: z.string().min(1, "ID token is required"),
});
export type GoogleAuthReq = z.infer<typeof GoogleAuthReqSchema>;

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
export type UserInfo = z.infer<typeof UserInfoSchema>;

/**
 * Google authentication response
 */
export const GoogleAuthResSchema = z.object({
  jwt: z.string().min(1, "JWT token is required"),
  user: UserInfoSchema,
});
export type GoogleAuthRes = z.infer<typeof GoogleAuthResSchema>;

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
export type UserProfile = z.infer<typeof UserProfileSchema>;

/**
 * Update profile response
 */
export const UpdateProfileResSchema = z.object({
  success: z.boolean(),
});
export type UpdateProfileRes = z.infer<typeof UpdateProfileResSchema>;

/**
 * Update profile request
 */
export const UpdateProfileReqSchema = z.object({
  displayName: z.string().min(1, "Display name is required").max(100, "Display name too long").optional(),
  bio: z.string().max(300, "Bio too long").optional(),
  hasApartment: z.boolean().optional(),
  openToMatching: z.boolean().optional(),
});
export type UpdateProfileReq = z.infer<typeof UpdateProfileReqSchema>;