import { z } from 'zod';
/**
 * Subscription status enum
 */
export declare const SubscriptionStatusSchema: z.ZodEnum<["ACTIVE", "NONE", "EXPIRED"]>;
export type SubscriptionStatus = z.infer<typeof SubscriptionStatusSchema>;
/**
 * Billing status response (feature-flag stub)
 */
export declare const BillingStatusResSchema: z.ZodObject<{
    status: z.ZodEnum<["ACTIVE", "NONE", "EXPIRED"]>;
    periodEnd: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    status: "ACTIVE" | "NONE" | "EXPIRED";
    periodEnd?: string | undefined;
}, {
    status: "ACTIVE" | "NONE" | "EXPIRED";
    periodEnd?: string | undefined;
}>;
export type BillingStatusRes = z.infer<typeof BillingStatusResSchema>;
/**
 * Billing webhook request (stub for payment provider)
 */
export declare const BillingWebhookReqSchema: z.ZodObject<{
    userId: z.ZodString;
    action: z.ZodEnum<["activate", "expire"]>;
    periodEnd: z.ZodOptional<z.ZodString>;
}, "strip", z.ZodTypeAny, {
    userId: string;
    action: "activate" | "expire";
    periodEnd?: string | undefined;
}, {
    userId: string;
    action: "activate" | "expire";
    periodEnd?: string | undefined;
}>;
export type BillingWebhookReq = z.infer<typeof BillingWebhookReqSchema>;
