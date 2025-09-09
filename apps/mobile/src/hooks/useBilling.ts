/**
 * Billing and subscription hooks
 * Handles subscription status and billing operations
 */

import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { z } from 'zod';
import { api } from '../lib/api';
import { useAuthStore } from '../store/auth';
import type { BillingStatus } from '../types/dto';

const billingStatusSchema = z.object({
  status: z.enum(["NONE", "ACTIVE", "EXPIRED", "CANCELED"]),
  periodEnd: z.string().datetime().optional(),
});

const fetchBillingStatus = async (): Promise<BillingStatus> => {
  const response = await api.get('/billing/status');
  
  // Validate response
  const validatedData = billingStatusSchema.parse(response.data);
  return validatedData;
};

export const useBillingStatus = () => {
  const { setSubscription } = useAuthStore();
  
  const query = useQuery({
    queryKey: ['billing', 'status'],
    queryFn: fetchBillingStatus,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
  
  // Handle success and error cases
  React.useEffect(() => {
    if (query.isSuccess && query.data) {
      setSubscription(query.data);
    } else if (query.isError) {
      console.error('Failed to fetch billing status:', query.error);
      setSubscription({ status: 'NONE' });
    }
  }, [query.isSuccess, query.isError, query.data, query.error, setSubscription]);
  
  return query;
};