import { z } from 'zod';
/**
 * Listing card for apartments feed
 * Note: title comes from place.formattedAddress (Hebrew), not user input
 */
export declare const ListingCardSchema: z.ZodObject<{
    id: z.ZodString;
    ownerUserId: z.ZodString;
    placeId: z.ZodString;
    title: z.ZodString;
    price: z.ZodNumber;
    attrs: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodUnknown>>;
    photos: z.ZodArray<z.ZodString, "many">;
    createdAt: z.ZodString;
    autoAccept: z.ZodOptional<z.ZodBoolean>;
}, "strip", z.ZodTypeAny, {
    id: string;
    createdAt: string;
    ownerUserId: string;
    placeId: string;
    title: string;
    price: number;
    photos: string[];
    attrs?: Record<string, unknown> | undefined;
    autoAccept?: boolean | undefined;
}, {
    id: string;
    createdAt: string;
    ownerUserId: string;
    placeId: string;
    title: string;
    price: number;
    photos: string[];
    attrs?: Record<string, unknown> | undefined;
    autoAccept?: boolean | undefined;
}>;
export type ListingCard = z.infer<typeof ListingCardSchema>;
/**
 * Apartments feed response
 */
export declare const ApartmentsFeedResSchema: z.ZodObject<{
    items: z.ZodArray<z.ZodObject<{
        id: z.ZodString;
        ownerUserId: z.ZodString;
        placeId: z.ZodString;
        title: z.ZodString;
        price: z.ZodNumber;
        attrs: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodUnknown>>;
        photos: z.ZodArray<z.ZodString, "many">;
        createdAt: z.ZodString;
        autoAccept: z.ZodOptional<z.ZodBoolean>;
    }, "strip", z.ZodTypeAny, {
        id: string;
        createdAt: string;
        ownerUserId: string;
        placeId: string;
        title: string;
        price: number;
        photos: string[];
        attrs?: Record<string, unknown> | undefined;
        autoAccept?: boolean | undefined;
    }, {
        id: string;
        createdAt: string;
        ownerUserId: string;
        placeId: string;
        title: string;
        price: number;
        photos: string[];
        attrs?: Record<string, unknown> | undefined;
        autoAccept?: boolean | undefined;
    }>, "many">;
    nextCursor: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    items: {
        id: string;
        createdAt: string;
        ownerUserId: string;
        placeId: string;
        title: string;
        price: number;
        photos: string[];
        attrs?: Record<string, unknown> | undefined;
        autoAccept?: boolean | undefined;
    }[];
    nextCursor?: string | undefined;
}, {
    items: {
        id: string;
        createdAt: string;
        ownerUserId: string;
        placeId: string;
        title: string;
        price: number;
        photos: string[];
        attrs?: Record<string, unknown> | undefined;
        autoAccept?: boolean | undefined;
    }[];
    nextCursor?: string | undefined;
}>;
export type ApartmentsFeedRes = z.infer<typeof ApartmentsFeedResSchema>;
/**
 * Create listing request
 * Note: title is derived from place.formattedAddress (Hebrew), not provided by user
 */
export declare const CreateListingReqSchema: z.ZodObject<{
    placeId: z.ZodString;
    price: z.ZodNumber;
    attrs: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodUnknown>>;
    photos: z.ZodArray<z.ZodString, "many">;
    autoAccept: z.ZodDefault<z.ZodOptional<z.ZodBoolean>>;
}, "strip", z.ZodTypeAny, {
    placeId: string;
    price: number;
    photos: string[];
    autoAccept: boolean;
    attrs?: Record<string, unknown> | undefined;
}, {
    placeId: string;
    price: number;
    photos: string[];
    attrs?: Record<string, unknown> | undefined;
    autoAccept?: boolean | undefined;
}>;
export type CreateListingReq = z.infer<typeof CreateListingReqSchema>;
/**
 * Create listing response
 */
export declare const CreateListingResSchema: z.ZodObject<{
    id: z.ZodString;
}, "strip", z.ZodTypeAny, {
    id: string;
}, {
    id: string;
}>;
export type CreateListingRes = z.infer<typeof CreateListingResSchema>;
/**
 * Listing swipe request
 */
export declare const ListingSwipeReqSchema: z.ZodObject<{
    listingId: z.ZodString;
    action: z.ZodEnum<["LIKE", "PASS"]>;
}, "strip", z.ZodTypeAny, {
    action: "LIKE" | "PASS";
    listingId: string;
}, {
    action: "LIKE" | "PASS";
    listingId: string;
}>;
export type ListingSwipeReq = z.infer<typeof ListingSwipeReqSchema>;
/**
 * Listing swipe response - returns matchId if conditions met
 * (autoAccept=true or owner has already liked the seeker)
 */
export declare const ListingSwipeResSchema: z.ZodObject<{
    matchId: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    matchId?: string | undefined;
}, {
    matchId?: string | undefined;
}>;
export type ListingSwipeRes = z.infer<typeof ListingSwipeResSchema>;
/**
 * Apartments feed query filters
 */
export declare const ApartmentsFeedFiltersSchema: z.ZodObject<{
    cursor: z.ZodOptional<z.ZodString>;
    limit: z.ZodDefault<z.ZodOptional<z.ZodNumber>>;
    city: z.ZodOptional<z.ZodString>;
    minPrice: z.ZodOptional<z.ZodNumber>;
    maxPrice: z.ZodOptional<z.ZodNumber>;
    rooms: z.ZodOptional<z.ZodNumber>;
    furnished: z.ZodOptional<z.ZodBoolean>;
    pets: z.ZodOptional<z.ZodBoolean>;
    smoking: z.ZodOptional<z.ZodBoolean>;
}, "strip", z.ZodTypeAny, {
    limit: number;
    cursor?: string | undefined;
    city?: string | undefined;
    minPrice?: number | undefined;
    maxPrice?: number | undefined;
    rooms?: number | undefined;
    furnished?: boolean | undefined;
    pets?: boolean | undefined;
    smoking?: boolean | undefined;
}, {
    cursor?: string | undefined;
    limit?: number | undefined;
    city?: string | undefined;
    minPrice?: number | undefined;
    maxPrice?: number | undefined;
    rooms?: number | undefined;
    furnished?: boolean | undefined;
    pets?: boolean | undefined;
    smoking?: boolean | undefined;
}>;
export type ApartmentsFeedFilters = z.infer<typeof ApartmentsFeedFiltersSchema>;
