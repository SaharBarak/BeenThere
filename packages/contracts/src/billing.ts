import { z } from 'zod';

/**
 * Subscription status enum
 */
export const SubscriptionStatusSchema = z.enum(['ACTIVE', 'NONE', 'EXPIRED']);
export type SubscriptionStatus = z.infer<typeof SubscriptionStatusSchema>;

/**
 * Billing status response (feature-flag stub)
 */
export const BillingStatusResSchema = z.object({
  status: SubscriptionStatusSchema,
  periodEnd: z.string().datetime().optional(),
});
export type BillingStatusRes = z.infer<typeof BillingStatusResSchema>;

/**
 * Billing webhook request (stub for payment provider)
 */
export const BillingWebhookReqSchema = z.object({
  userId: z.string().uuid(),
  action: z.enum(['activate', 'expire']),
  periodEnd: z.string().datetime().optional(),
});
export type BillingWebhookReq = z.infer<typeof BillingWebhookReqSchema>;