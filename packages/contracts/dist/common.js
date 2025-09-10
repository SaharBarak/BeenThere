import { z } from 'zod';
/**
 * PlaceRef - must have googlePlaceId OR both lat & lng
 * Used to reference places in requests
 */
export const PlaceRefSchema = z.object({
    googlePlaceId: z.string().optional(),
    formattedAddress: z.string().optional(),
    lat: z.number().optional(),
    lng: z.number().optional(),
}).refine((data) => data.googlePlaceId || (data.lat !== undefined && data.lng !== undefined), {
    message: "Must provide either googlePlaceId or both lat and lng",
});
/**
 * UUID string validation
 */
export const UuidSchema = z.string().uuid();
/**
 * Score validation (1-10 integers)
 */
export const ScoreSchema = z.number().int().min(1).max(10);
/**
 * Date string validation (YYYY-MM-DD format)
 */
export const DateStringSchema = z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "Date must be in YYYY-MM-DD format");
/**
 * Common response wrapper for paginated results
 */
export const PaginatedResponseSchema = (itemSchema) => z.object({
    items: z.array(itemSchema),
    nextCursor: z.string().optional(),
});
