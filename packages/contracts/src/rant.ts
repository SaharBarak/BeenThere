import { z } from 'zod';
import { PlaceRefSchema, UuidSchema, ScoreSchema, DateStringSchema } from './common.js';
import { LandlordScoresSchema, ApartmentScoresSchema, ExtrasSchema } from './places.js';

/**
 * Combined Rant Request - Landlord + Apartment together (single submission)
 */
export const CreateRantCombinedReqSchema = z.object({
  landlordPhone: z.string().min(1, "Landlord phone is required"),
  periodStart: DateStringSchema.optional(),
  periodEnd: DateStringSchema.optional(),
  isCurrentResidence: z.boolean().optional(),
  landlordScores: LandlordScoresSchema,
  apartmentScores: ApartmentScoresSchema,
  extras: ExtrasSchema.optional(),
  comment: z.string().max(300).optional(),
  place: PlaceRefSchema,
});
export type CreateRantCombinedReq = z.infer<typeof CreateRantCombinedReqSchema>;

/**
 * Combined Rant Response
 */
export const CreateRantCombinedResSchema = z.object({
  rantGroupId: UuidSchema,
});
export type CreateRantCombinedRes = z.infer<typeof CreateRantCombinedResSchema>;

/**
 * Roommate rating scores
 */
export const RoommateScoresSchema = z.object({
  cleanliness: ScoreSchema,
  communication: ScoreSchema,
  reliability: ScoreSchema,
  respect: ScoreSchema,
  costSharing: ScoreSchema,
});
export type RoommateScores = z.infer<typeof RoommateScoresSchema>;

/**
 * Roommate hint for cases where we don't have a user ID
 */
export const RateeHintSchema = z.object({
  name: z.string().min(1, "Name is required"),
  org: z.string().optional(),
});
export type RateeHint = z.infer<typeof RateeHintSchema>;

/**
 * Roommate Rant Request - separate from landlord+apartment
 */
export const CreateRoommateRantReqSchema = z.object({
  rateeUserId: UuidSchema.optional(),
  rateeHint: RateeHintSchema.optional(),
  scores: RoommateScoresSchema,
  comment: z.string().optional(),
}).refine(
  (data) => data.rateeUserId || data.rateeHint,
  {
    message: "Must provide either rateeUserId or rateeHint",
  }
);
export type CreateRoommateRantReq = z.infer<typeof CreateRoommateRantReqSchema>;

/**
 * Roommate Rant Response
 */
export const CreateRoommateRantResSchema = z.object({
  ratingId: UuidSchema,
});
export type CreateRoommateRantRes = z.infer<typeof CreateRoommateRantResSchema>;