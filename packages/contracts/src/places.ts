import { z } from 'zod';
import { PlaceRefSchema, UuidSchema, ScoreSchema } from './common.js';

/**
 * PlaceSnap Request - snap a place from Google Places to our DB
 */
export const PlaceSnapReqSchema = PlaceRefSchema;
export type PlaceSnapReq = z.infer<typeof PlaceSnapReqSchema>;

/**
 * PlaceSnap Response
 */
export const PlaceSnapResSchema = z.object({
  placeId: UuidSchema,
});
export type PlaceSnapRes = z.infer<typeof PlaceSnapResSchema>;

/**
 * Place entity for responses
 */
export const PlaceSchema = z.object({
  id: UuidSchema,
  googlePlaceId: z.string().optional(),
  formattedAddress: z.string().optional(),
  lat: z.number().optional(),
  lng: z.number().optional(),
});
export type Place = z.infer<typeof PlaceSchema>;

/**
 * Rating scores for landlords
 */
export const LandlordScoresSchema = z.object({
  fairness: ScoreSchema,
  response: ScoreSchema,
  maintenance: ScoreSchema,
  privacy: ScoreSchema,
});
export type LandlordScores = z.infer<typeof LandlordScoresSchema>;

/**
 * Rating scores for apartments
 */
export const ApartmentScoresSchema = z.object({
  condition: ScoreSchema,
  noise: ScoreSchema,
  utilities: ScoreSchema,
  sunlightMold: ScoreSchema,
});
export type ApartmentScores = z.infer<typeof ApartmentScoresSchema>;

/**
 * Extra rating fields (all optional)
 */
export const ExtrasSchema = z.object({
  neighborsNoise: ScoreSchema.optional(),
  roofCommon: ScoreSchema.optional(),
  elevatorSolar: ScoreSchema.optional(),
  neighSafety: ScoreSchema.optional(),
  neighServices: ScoreSchema.optional(),
  neighTransit: ScoreSchema.optional(),
  priceFairness: ScoreSchema.optional(),
});
export type Extras = z.infer<typeof ExtrasSchema>;

/**
 * Recent rating entry for place profiles
 */
export const RecentRatingSchema = z.object({
  at: z.string().datetime(),
  landlordScores: LandlordScoresSchema.optional(),
  apartmentScores: ApartmentScoresSchema.optional(),
  extras: ExtrasSchema.optional(),
  comment: z.string().optional(),
});
export type RecentRating = z.infer<typeof RecentRatingSchema>;

/**
 * Place Profile Response - shown when tapping marker or viewing place
 */
export const PlaceProfileResSchema = z.object({
  place: PlaceSchema,
  ratings: z.object({
    counts: z.object({
      landlord: z.number().int().nonnegative(),
      apartment: z.number().int().nonnegative(),
    }),
    averages: z.object({
      landlord: z.number().optional(),
      apartment: z.number().optional(),
      extras: z.record(z.string(), z.number()).optional(),
    }),
    recent: z.array(RecentRatingSchema),
  }),
});
export type PlaceProfileRes = z.infer<typeof PlaceProfileResSchema>;