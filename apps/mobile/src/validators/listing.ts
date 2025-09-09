/**
 * Zod validators for listing-related data
 * Ensures type safety and runtime validation
 */

import { z } from 'zod';

export const listingTypeSchema = z.enum(["ROOMMATE_GROUP", "ENTIRE_PLACE"]);

export const listingCardSchema = z.object({
  id: z.string().uuid(),
  type: listingTypeSchema,
  title: z.string().min(1, "Title is required"),
  city: z.string().min(1, "City is required"),
  price: z.number().int().nonnegative("Price must be non-negative"),
  createdAt: z.string().datetime("Invalid date format"),
});

export const feedResponseSchema = z.object({
  items: z.array(listingCardSchema),
  nextCursor: z.string().optional().nullable(),
});

// Export types derived from schemas
export type ListingCard = z.infer<typeof listingCardSchema>;
export type FeedResponse = z.infer<typeof feedResponseSchema>;