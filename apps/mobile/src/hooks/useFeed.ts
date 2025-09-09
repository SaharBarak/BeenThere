/**
 * Feed data fetching hook using TanStack Query
 * Handles cursor-based pagination for listing feed
 */

import { useInfiniteQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import { feedResponseSchema } from '../validators/listing';
import type { ListingCardDTO, FeedResponse } from '../types/dto';

interface FeedParams {
  city?: string;
  minBudget?: number;
  maxBudget?: number;
  limit?: number;
}

const fetchFeed = async ({ 
  pageParam, 
  ...params 
}: FeedParams & { pageParam?: string }): Promise<FeedResponse<ListingCardDTO>> => {
  const searchParams = new URLSearchParams();
  
  // Add filter params
  if (params.city) searchParams.set('city', params.city);
  if (params.minBudget) searchParams.set('minBudget', params.minBudget.toString());
  if (params.maxBudget) searchParams.set('maxBudget', params.maxBudget.toString());
  if (params.limit) searchParams.set('limit', params.limit.toString());
  
  // Add cursor for pagination
  if (pageParam) searchParams.set('cursor', pageParam);
  
  const response = await api.get(`/listings/feed?${searchParams}`);
  
  // Validate response with zod
  const validatedData = feedResponseSchema.parse(response.data);
  return validatedData;
};

export const useFeed = (params: FeedParams = {}) => {
  return useInfiniteQuery({
    queryKey: ['feed', params],
    queryFn: ({ pageParam }) => fetchFeed({ ...params, pageParam }),
    initialPageParam: undefined as string | undefined,
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    staleTime: 1000 * 60 * 2, // 2 minutes
  });
};

// Helper hook to get flattened listings
export const useFeedListings = (params: FeedParams = {}) => {
  const query = useFeed(params);
  
  const listings = query.data?.pages.flatMap(page => page.items) ?? [];
  
  return {
    ...query,
    listings,
  };
};