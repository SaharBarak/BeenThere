import { z } from 'zod';
/**
 * PlaceRef - must have googlePlaceId OR both lat & lng
 * Used to reference places in requests
 */
export declare const PlaceRefSchema: z.ZodEffects<z.ZodObject<{
    googlePlaceId: z.ZodOptional<z.ZodString>;
    formattedAddress: z.ZodOptional<z.ZodString>;
    lat: z.ZodOptional<z.ZodNumber>;
    lng: z.ZodOptional<z.ZodNumber>;
}, "strip", z.ZodTypeAny, {
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}, {
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}>, {
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}, {
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}>;
export type PlaceRef = z.infer<typeof PlaceRefSchema>;
/**
 * UUID string validation
 */
export declare const UuidSchema: z.ZodString;
/**
 * Score validation (1-10 integers)
 */
export declare const ScoreSchema: z.ZodNumber;
/**
 * Date string validation (YYYY-MM-DD format)
 */
export declare const DateStringSchema: z.ZodString;
/**
 * Common response wrapper for paginated results
 */
export declare const PaginatedResponseSchema: <T extends z.ZodTypeAny>(itemSchema: T) => z.ZodObject<{
    items: z.ZodArray<T, "many">;
    nextCursor: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    items: T["_output"][];
    nextCursor?: string | undefined;
}, {
    items: T["_input"][];
    nextCursor?: string | undefined;
}>;
export type PaginatedResponse<T> = {
    items: T[];
    nextCursor?: string;
};
