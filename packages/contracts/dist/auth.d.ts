import { z } from 'zod';
/**
 * Google authentication request
 */
export declare const GoogleAuthReqSchema: z.ZodObject<{
    idToken: z.ZodString;
}, "strip", z.ZodTypeAny, {
    idToken: string;
}, {
    idToken: string;
}>;
export type GoogleAuthReq = z.infer<typeof GoogleAuthReqSchema>;
/**
 * User info returned from authentication
 */
export declare const UserInfoSchema: z.ZodObject<{
    id: z.ZodString;
    email: z.ZodString;
    displayName: z.ZodString;
    photoUrl: z.ZodOptional<z.ZodString>;
    bio: z.ZodOptional<z.ZodString>;
    hasApartment: z.ZodDefault<z.ZodBoolean>;
    openToMatching: z.ZodDefault<z.ZodBoolean>;
    createdAt: z.ZodString;
}, "strip", z.ZodTypeAny, {
    displayName: string;
    hasApartment: boolean;
    id: string;
    createdAt: string;
    email: string;
    openToMatching: boolean;
    photoUrl?: string | undefined;
    bio?: string | undefined;
}, {
    displayName: string;
    id: string;
    createdAt: string;
    email: string;
    photoUrl?: string | undefined;
    bio?: string | undefined;
    hasApartment?: boolean | undefined;
    openToMatching?: boolean | undefined;
}>;
export type UserInfo = z.infer<typeof UserInfoSchema>;
/**
 * Google authentication response
 */
export declare const GoogleAuthResSchema: z.ZodObject<{
    jwt: z.ZodString;
    user: z.ZodObject<{
        id: z.ZodString;
        email: z.ZodString;
        displayName: z.ZodString;
        photoUrl: z.ZodOptional<z.ZodString>;
        bio: z.ZodOptional<z.ZodString>;
        hasApartment: z.ZodDefault<z.ZodBoolean>;
        openToMatching: z.ZodDefault<z.ZodBoolean>;
        createdAt: z.ZodString;
    }, "strip", z.ZodTypeAny, {
        displayName: string;
        hasApartment: boolean;
        id: string;
        createdAt: string;
        email: string;
        openToMatching: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    }, {
        displayName: string;
        id: string;
        createdAt: string;
        email: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        hasApartment?: boolean | undefined;
        openToMatching?: boolean | undefined;
    }>;
}, "strip", z.ZodTypeAny, {
    jwt: string;
    user: {
        displayName: string;
        hasApartment: boolean;
        id: string;
        createdAt: string;
        email: string;
        openToMatching: boolean;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    };
}, {
    jwt: string;
    user: {
        displayName: string;
        id: string;
        createdAt: string;
        email: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
        hasApartment?: boolean | undefined;
        openToMatching?: boolean | undefined;
    };
}>;
export type GoogleAuthRes = z.infer<typeof GoogleAuthResSchema>;
/**
 * User profile for display (public info)
 */
export declare const UserProfileSchema: z.ZodObject<{
    user: z.ZodObject<{
        id: z.ZodString;
        displayName: z.ZodString;
        photoUrl: z.ZodOptional<z.ZodString>;
        bio: z.ZodOptional<z.ZodString>;
    }, "strip", z.ZodTypeAny, {
        displayName: string;
        id: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    }, {
        displayName: string;
        id: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    }>;
    ratingsSummary: z.ZodObject<{
        roommateAvg: z.ZodOptional<z.ZodNumber>;
        count: z.ZodNumber;
    }, "strip", z.ZodTypeAny, {
        count: number;
        roommateAvg?: number | undefined;
    }, {
        count: number;
        roommateAvg?: number | undefined;
    }>;
}, "strip", z.ZodTypeAny, {
    user: {
        displayName: string;
        id: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    };
    ratingsSummary: {
        count: number;
        roommateAvg?: number | undefined;
    };
}, {
    user: {
        displayName: string;
        id: string;
        photoUrl?: string | undefined;
        bio?: string | undefined;
    };
    ratingsSummary: {
        count: number;
        roommateAvg?: number | undefined;
    };
}>;
export type UserProfile = z.infer<typeof UserProfileSchema>;
/**
 * Update profile response
 */
export declare const UpdateProfileResSchema: z.ZodObject<{
    success: z.ZodBoolean;
}, "strip", z.ZodTypeAny, {
    success: boolean;
}, {
    success: boolean;
}>;
export type UpdateProfileRes = z.infer<typeof UpdateProfileResSchema>;
/**
 * Update profile request
 */
export declare const UpdateProfileReqSchema: z.ZodObject<{
    displayName: z.ZodOptional<z.ZodString>;
    bio: z.ZodOptional<z.ZodString>;
    hasApartment: z.ZodOptional<z.ZodBoolean>;
    openToMatching: z.ZodOptional<z.ZodBoolean>;
}, "strip", z.ZodTypeAny, {
    displayName?: string | undefined;
    bio?: string | undefined;
    hasApartment?: boolean | undefined;
    openToMatching?: boolean | undefined;
}, {
    displayName?: string | undefined;
    bio?: string | undefined;
    hasApartment?: boolean | undefined;
    openToMatching?: boolean | undefined;
}>;
export type UpdateProfileReq = z.infer<typeof UpdateProfileReqSchema>;
