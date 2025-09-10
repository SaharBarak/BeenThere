import { z } from 'zod';
/**
 * Combined Rant Request - Landlord + Apartment together (single submission)
 */
export declare const CreateRantCombinedReqSchema: z.ZodObject<{
    landlordPhone: z.ZodString;
    periodStart: z.ZodOptional<z.ZodString>;
    periodEnd: z.ZodOptional<z.ZodString>;
    isCurrentResidence: z.ZodOptional<z.ZodBoolean>;
    landlordScores: z.ZodObject<{
        fairness: z.ZodNumber;
        response: z.ZodNumber;
        maintenance: z.ZodNumber;
        privacy: z.ZodNumber;
    }, "strip", z.ZodTypeAny, {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    }, {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    }>;
    apartmentScores: z.ZodObject<{
        condition: z.ZodNumber;
        noise: z.ZodNumber;
        utilities: z.ZodNumber;
        sunlightMold: z.ZodNumber;
    }, "strip", z.ZodTypeAny, {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    }, {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    }>;
    extras: z.ZodOptional<z.ZodObject<{
        neighborsNoise: z.ZodOptional<z.ZodNumber>;
        roofCommon: z.ZodOptional<z.ZodNumber>;
        elevatorSolar: z.ZodOptional<z.ZodNumber>;
        neighSafety: z.ZodOptional<z.ZodNumber>;
        neighServices: z.ZodOptional<z.ZodNumber>;
        neighTransit: z.ZodOptional<z.ZodNumber>;
        priceFairness: z.ZodOptional<z.ZodNumber>;
    }, "strip", z.ZodTypeAny, {
        neighborsNoise?: number | undefined;
        roofCommon?: number | undefined;
        elevatorSolar?: number | undefined;
        neighSafety?: number | undefined;
        neighServices?: number | undefined;
        neighTransit?: number | undefined;
        priceFairness?: number | undefined;
    }, {
        neighborsNoise?: number | undefined;
        roofCommon?: number | undefined;
        elevatorSolar?: number | undefined;
        neighSafety?: number | undefined;
        neighServices?: number | undefined;
        neighTransit?: number | undefined;
        priceFairness?: number | undefined;
    }>>;
    comment: z.ZodOptional<z.ZodString>;
    place: z.ZodEffects<z.ZodObject<{
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
}, "strip", z.ZodTypeAny, {
    landlordScores: {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    };
    apartmentScores: {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    };
    place: {
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    };
    landlordPhone: string;
    periodEnd?: string | undefined;
    extras?: {
        neighborsNoise?: number | undefined;
        roofCommon?: number | undefined;
        elevatorSolar?: number | undefined;
        neighSafety?: number | undefined;
        neighServices?: number | undefined;
        neighTransit?: number | undefined;
        priceFairness?: number | undefined;
    } | undefined;
    comment?: string | undefined;
    periodStart?: string | undefined;
    isCurrentResidence?: boolean | undefined;
}, {
    landlordScores: {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    };
    apartmentScores: {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    };
    place: {
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    };
    landlordPhone: string;
    periodEnd?: string | undefined;
    extras?: {
        neighborsNoise?: number | undefined;
        roofCommon?: number | undefined;
        elevatorSolar?: number | undefined;
        neighSafety?: number | undefined;
        neighServices?: number | undefined;
        neighTransit?: number | undefined;
        priceFairness?: number | undefined;
    } | undefined;
    comment?: string | undefined;
    periodStart?: string | undefined;
    isCurrentResidence?: boolean | undefined;
}>;
export type CreateRantCombinedReq = z.infer<typeof CreateRantCombinedReqSchema>;
/**
 * Combined Rant Response
 */
export declare const CreateRantCombinedResSchema: z.ZodObject<{
    rantGroupId: z.ZodString;
}, "strip", z.ZodTypeAny, {
    rantGroupId: string;
}, {
    rantGroupId: string;
}>;
export type CreateRantCombinedRes = z.infer<typeof CreateRantCombinedResSchema>;
/**
 * Roommate rating scores
 */
export declare const RoommateScoresSchema: z.ZodObject<{
    cleanliness: z.ZodNumber;
    communication: z.ZodNumber;
    reliability: z.ZodNumber;
    respect: z.ZodNumber;
    costSharing: z.ZodNumber;
}, "strip", z.ZodTypeAny, {
    cleanliness: number;
    communication: number;
    reliability: number;
    respect: number;
    costSharing: number;
}, {
    cleanliness: number;
    communication: number;
    reliability: number;
    respect: number;
    costSharing: number;
}>;
export type RoommateScores = z.infer<typeof RoommateScoresSchema>;
/**
 * Roommate hint for cases where we don't have a user ID
 */
export declare const RateeHintSchema: z.ZodObject<{
    name: z.ZodString;
    org: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    name: string;
    org?: string | undefined;
}, {
    name: string;
    org?: string | undefined;
}>;
export type RateeHint = z.infer<typeof RateeHintSchema>;
/**
 * Roommate Rant Request - separate from landlord+apartment
 */
export declare const CreateRoommateRantReqSchema: z.ZodEffects<z.ZodObject<{
    rateeUserId: z.ZodOptional<z.ZodString>;
    rateeHint: z.ZodOptional<z.ZodObject<{
        name: z.ZodString;
        org: z.ZodOptional<z.ZodString>;
    }, "strip", z.ZodTypeAny, {
        name: string;
        org?: string | undefined;
    }, {
        name: string;
        org?: string | undefined;
    }>>;
    scores: z.ZodObject<{
        cleanliness: z.ZodNumber;
        communication: z.ZodNumber;
        reliability: z.ZodNumber;
        respect: z.ZodNumber;
        costSharing: z.ZodNumber;
    }, "strip", z.ZodTypeAny, {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    }, {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    }>;
    comment: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    scores: {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    };
    comment?: string | undefined;
    rateeUserId?: string | undefined;
    rateeHint?: {
        name: string;
        org?: string | undefined;
    } | undefined;
}, {
    scores: {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    };
    comment?: string | undefined;
    rateeUserId?: string | undefined;
    rateeHint?: {
        name: string;
        org?: string | undefined;
    } | undefined;
}>, {
    scores: {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    };
    comment?: string | undefined;
    rateeUserId?: string | undefined;
    rateeHint?: {
        name: string;
        org?: string | undefined;
    } | undefined;
}, {
    scores: {
        cleanliness: number;
        communication: number;
        reliability: number;
        respect: number;
        costSharing: number;
    };
    comment?: string | undefined;
    rateeUserId?: string | undefined;
    rateeHint?: {
        name: string;
        org?: string | undefined;
    } | undefined;
}>;
export type CreateRoommateRantReq = z.infer<typeof CreateRoommateRantReqSchema>;
/**
 * Roommate Rant Response
 */
export declare const CreateRoommateRantResSchema: z.ZodObject<{
    ratingId: z.ZodString;
}, "strip", z.ZodTypeAny, {
    ratingId: string;
}, {
    ratingId: string;
}>;
export type CreateRoommateRantRes = z.infer<typeof CreateRoommateRantResSchema>;
