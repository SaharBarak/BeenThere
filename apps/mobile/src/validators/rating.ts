/**
 * Zod validators for rating-related data
 * Handles house, roommate, and landlord ratings
 */

import { z } from 'zod';

export const ratingTargetTypeSchema = z.enum(['HOUSE', 'ROOMMATE', 'LANDLORD']);

// Base rating schema
export const baseRatingSchema = z.object({
  targetType: ratingTargetTypeSchema,
  targetId: z.string().uuid('Invalid target ID'),
  comment: z.string().optional(),
});

// House rating categories
export const houseScoresSchema = z.object({
  location: z.number().min(1).max(5).int(),
  cleanliness: z.number().min(1).max(5).int(),
  amenities: z.number().min(1).max(5).int(),
  noiseLevel: z.number().min(1).max(5).int(),
  valueForMoney: z.number().min(1).max(5).int(),
});

// Roommate rating categories
export const roommateScoresSchema = z.object({
  communication: z.number().min(1).max(5).int(),
  cleanliness: z.number().min(1).max(5).int(),
  respectForSpace: z.number().min(1).max(5).int(),
  socialCompatibility: z.number().min(1).max(5).int(),
  reliability: z.number().min(1).max(5).int(),
});

// Landlord rating categories
export const landlordScoresSchema = z.object({
  responsiveness: z.number().min(1).max(5).int(),
  fairness: z.number().min(1).max(5).int(),
  maintenance: z.number().min(1).max(5).int(),
  transparency: z.number().min(1).max(5).int(),
  professionalism: z.number().min(1).max(5).int(),
});

// Combined rating request schema
export const ratingRequestSchema = z.discriminatedUnion('targetType', [
  z.object({
    targetType: z.literal('HOUSE'),
    targetId: z.string().uuid(),
    scores: houseScoresSchema,
    comment: z.string().optional(),
  }),
  z.object({
    targetType: z.literal('ROOMMATE'),
    targetId: z.string().uuid(),
    scores: roommateScoresSchema,
    comment: z.string().optional(),
  }),
  z.object({
    targetType: z.literal('LANDLORD'),
    targetId: z.string().uuid(),
    scores: landlordScoresSchema,
    comment: z.string().optional(),
  }),
]);

// Response schema
export const ratingResponseSchema = z.object({
  id: z.string().uuid(),
  targetType: ratingTargetTypeSchema,
  targetId: z.string().uuid(),
  raterId: z.string().uuid(),
  scores: z.record(z.string(), z.number()),
  comment: z.string().optional(),
  createdAt: z.string().datetime(),
});

// Export types
export type RatingTargetType = z.infer<typeof ratingTargetTypeSchema>;
export type HouseScores = z.infer<typeof houseScoresSchema>;
export type RoommateScores = z.infer<typeof roommateScoresSchema>;
export type LandlordScores = z.infer<typeof landlordScoresSchema>;
export type RatingRequest = z.infer<typeof ratingRequestSchema>;
export type RatingResponse = z.infer<typeof ratingResponseSchema>;

// Helper function to get score categories for each type
export const getRatingCategories = (targetType: RatingTargetType) => {
  switch (targetType) {
    case 'HOUSE':
      return {
        location: 'Location',
        cleanliness: 'Cleanliness',
        amenities: 'Amenities',
        noiseLevel: 'Noise Level',
        valueForMoney: 'Value for Money',
      };
    case 'ROOMMATE':
      return {
        communication: 'Communication',
        cleanliness: 'Cleanliness',
        respectForSpace: 'Respect for Space',
        socialCompatibility: 'Social Compatibility',
        reliability: 'Reliability',
      };
    case 'LANDLORD':
      return {
        responsiveness: 'Responsiveness',
        fairness: 'Fairness',
        maintenance: 'Maintenance Quality',
        transparency: 'Lease Transparency',
        professionalism: 'Professionalism',
      };
  }
};