import { z } from 'zod';
/**
 * Subscription status enum
 */
export const SubscriptionStatusSchema = z.enum(['ACTIVE', 'NONE', 'EXPIRED']);
/**
 * Billing status response (feature-flag stub)
 */
export const BillingStatusResSchema = z.object({
    status: SubscriptionStatusSchema,
    periodEnd: z.string().datetime().optional(),
});
/**
 * Billing webhook request (stub for payment provider)
 */
export const BillingWebhookReqSchema = z.object({
    userId: z.string().uuid(),
    action: z.enum(['activate', 'expire']),
    periodEnd: z.string().datetime().optional(),
});
