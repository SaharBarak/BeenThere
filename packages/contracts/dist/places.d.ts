import { z } from 'zod';
/**
 * PlaceSnap Request - snap a place from Google Places to our DB
 */
export declare const PlaceSnapReqSchema: z.ZodEffects<z.ZodObject<{
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
export type PlaceSnapReq = z.infer<typeof PlaceSnapReqSchema>;
/**
 * PlaceSnap Response
 */
export declare const PlaceSnapResSchema: z.ZodObject<{
    placeId: z.ZodString;
}, "strip", z.ZodTypeAny, {
    placeId: string;
}, {
    placeId: string;
}>;
export type PlaceSnapRes = z.infer<typeof PlaceSnapResSchema>;
/**
 * Place entity for responses
 */
export declare const PlaceSchema: z.ZodObject<{
    id: z.ZodString;
    googlePlaceId: z.ZodOptional<z.ZodString>;
    formattedAddress: z.ZodOptional<z.ZodString>;
    lat: z.ZodOptional<z.ZodNumber>;
    lng: z.ZodOptional<z.ZodNumber>;
}, "strip", z.ZodTypeAny, {
    id: string;
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}, {
    id: string;
    googlePlaceId?: string | undefined;
    formattedAddress?: string | undefined;
    lat?: number | undefined;
    lng?: number | undefined;
}>;
export type Place = z.infer<typeof PlaceSchema>;
/**
 * Rating scores for landlords
 */
export declare const LandlordScoresSchema: z.ZodObject<{
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
export type LandlordScores = z.infer<typeof LandlordScoresSchema>;
/**
 * Rating scores for apartments
 */
export declare const ApartmentScoresSchema: z.ZodObject<{
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
export type ApartmentScores = z.infer<typeof ApartmentScoresSchema>;
/**
 * Extra rating fields (all optional)
 */
export declare const ExtrasSchema: z.ZodObject<{
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
}>;
export type Extras = z.infer<typeof ExtrasSchema>;
/**
 * Recent rating entry for place profiles
 */
export declare const RecentRatingSchema: z.ZodObject<{
    at: z.ZodString;
    landlordScores: z.ZodOptional<z.ZodObject<{
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
    }>>;
    apartmentScores: z.ZodOptional<z.ZodObject<{
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
    }>>;
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
}, "strip", z.ZodTypeAny, {
    at: string;
    landlordScores?: {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    } | undefined;
    apartmentScores?: {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    } | undefined;
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
}, {
    at: string;
    landlordScores?: {
        fairness: number;
        response: number;
        maintenance: number;
        privacy: number;
    } | undefined;
    apartmentScores?: {
        condition: number;
        noise: number;
        utilities: number;
        sunlightMold: number;
    } | undefined;
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
}>;
export type RecentRating = z.infer<typeof RecentRatingSchema>;
/**
 * Place Profile Response - shown when tapping marker or viewing place
 */
export declare const PlaceProfileResSchema: z.ZodObject<{
    place: z.ZodObject<{
        id: z.ZodString;
        googlePlaceId: z.ZodOptional<z.ZodString>;
        formattedAddress: z.ZodOptional<z.ZodString>;
        lat: z.ZodOptional<z.ZodNumber>;
        lng: z.ZodOptional<z.ZodNumber>;
    }, "strip", z.ZodTypeAny, {
        id: string;
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    }, {
        id: string;
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    }>;
    ratings: z.ZodObject<{
        counts: z.ZodObject<{
            landlord: z.ZodNumber;
            apartment: z.ZodNumber;
        }, "strip", z.ZodTypeAny, {
            landlord: number;
            apartment: number;
        }, {
            landlord: number;
            apartment: number;
        }>;
        averages: z.ZodObject<{
            landlord: z.ZodOptional<z.ZodNumber>;
            apartment: z.ZodOptional<z.ZodNumber>;
            extras: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodNumber>>;
        }, "strip", z.ZodTypeAny, {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        }, {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        }>;
        recent: z.ZodArray<z.ZodObject<{
            at: z.ZodString;
            landlordScores: z.ZodOptional<z.ZodObject<{
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
            }>>;
            apartmentScores: z.ZodOptional<z.ZodObject<{
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
            }>>;
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
        }, "strip", z.ZodTypeAny, {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }, {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }>, "many">;
    }, "strip", z.ZodTypeAny, {
        counts: {
            landlord: number;
            apartment: number;
        };
        averages: {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        };
        recent: {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }[];
    }, {
        counts: {
            landlord: number;
            apartment: number;
        };
        averages: {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        };
        recent: {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }[];
    }>;
}, "strip", z.ZodTypeAny, {
    place: {
        id: string;
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    };
    ratings: {
        counts: {
            landlord: number;
            apartment: number;
        };
        averages: {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        };
        recent: {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }[];
    };
}, {
    place: {
        id: string;
        googlePlaceId?: string | undefined;
        formattedAddress?: string | undefined;
        lat?: number | undefined;
        lng?: number | undefined;
    };
    ratings: {
        counts: {
            landlord: number;
            apartment: number;
        };
        averages: {
            extras?: Record<string, number> | undefined;
            landlord?: number | undefined;
            apartment?: number | undefined;
        };
        recent: {
            at: string;
            landlordScores?: {
                fairness: number;
                response: number;
                maintenance: number;
                privacy: number;
            } | undefined;
            apartmentScores?: {
                condition: number;
                noise: number;
                utilities: number;
                sunlightMold: number;
            } | undefined;
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
        }[];
    };
}>;
export type PlaceProfileRes = z.infer<typeof PlaceProfileResSchema>;
