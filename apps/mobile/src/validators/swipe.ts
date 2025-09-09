/**
 * Zod validators for swipe-related data
 * Handles swipe actions and match responses
 */

import { z } from 'zod';

export const swipeTargetSchema = z.enum(["USER", "LISTING"]);
export const swipeActionSchema = z.enum(["LIKE", "PASS"]);

export const swipeRequestSchema = z.object({
  targetType: swipeTargetSchema,
  targetId: z.string().uuid("Invalid target ID"),
  action: swipeActionSchema,
});

export const swipeResponseSchema = z.object({
  matchId: z.string().uuid().optional().nullable(),
});

// Export types
export type SwipeRequest = z.infer<typeof swipeRequestSchema>;
export type SwipeResponse = z.infer<typeof swipeResponseSchema>;