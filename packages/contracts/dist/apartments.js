import { z } from 'zod';
import { UuidSchema, PaginatedResponseSchema } from './common.js';
import { SwipeActionSchema } from './roommates.js';
/**
 * Listing card for apartments feed
 * Note: title comes from place.formattedAddress (Hebrew), not user input
 */
export const ListingCardSchema = z.object({
    id: UuidSchema,
    ownerUserId: UuidSchema,
    placeId: UuidSchema,
    title: z.string().min(1, "Title is required"), // Hebrew address from place
    price: z.number().positive("Price must be positive"),
    attrs: z.record(z.string(), z.unknown()).optional(),
    photos: z.array(z.string().url()),
    createdAt: z.string().datetime(),
    autoAccept: z.boolean().optional(),
});
/**
 * Apartments feed response
 */
export const ApartmentsFeedResSchema = PaginatedResponseSchema(ListingCardSchema);
/**
 * Create listing request
 * Note: title is derived from place.formattedAddress (Hebrew), not provided by user
 */
export const CreateListingReqSchema = z.object({
    placeId: UuidSchema,
    price: z.number().positive("Price must be positive"),
    attrs: z.record(z.string(), z.unknown()).optional(),
    photos: z.array(z.string().url()).min(1, "At least one photo is required"),
    autoAccept: z.boolean().optional().default(false),
});
/**
 * Create listing response
 */
export const CreateListingResSchema = z.object({
    id: UuidSchema,
});
/**
 * Listing swipe request
 */
export const ListingSwipeReqSchema = z.object({
    listingId: UuidSchema,
    action: SwipeActionSchema,
});
/**
 * Listing swipe response - returns matchId if conditions met
 * (autoAccept=true or owner has already liked the seeker)
 */
export const ListingSwipeResSchema = z.object({
    matchId: UuidSchema.optional(),
});
/**
 * Apartments feed query filters
 */
export const ApartmentsFeedFiltersSchema = z.object({
    cursor: z.string().optional(),
    limit: z.number().int().min(1).max(50).optional().default(20),
    city: z.string().optional(),
    minPrice: z.number().nonnegative().optional(),
    maxPrice: z.number().positive().optional(),
    rooms: z.number().int().positive().optional(),
    furnished: z.boolean().optional(),
    pets: z.boolean().optional(),
    smoking: z.boolean().optional(),
});
