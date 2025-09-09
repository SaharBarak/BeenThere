import { QueryClient, QueryClientConfig } from '@tanstack/react-query';

// Query client configuration
const queryConfig: QueryClientConfig = {
  defaultOptions: {
    queries: {
      // Time in milliseconds that data remains fresh
      staleTime: 5 * 60 * 1000, // 5 minutes
      
      // Time in milliseconds that unused/inactive cache data remains in memory
      gcTime: 10 * 60 * 1000, // 10 minutes (formerly cacheTime)
      
      // Retry failed requests
      retry: (failureCount, error: any) => {
        // Don't retry on 4xx errors (client errors)
        if (error?.response?.status >= 400 && error?.response?.status < 500) {
          return false;
        }
        // Retry up to 3 times for other errors
        return failureCount < 3;
      },
      
      // Retry delay with exponential backoff
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      
      // Refetch on window focus
      refetchOnWindowFocus: false,
      
      // Refetch on reconnect
      refetchOnReconnect: true,
    },
    mutations: {
      // Retry failed mutations
      retry: (failureCount, error: any) => {
        // Don't retry on 4xx errors
        if (error?.response?.status >= 400 && error?.response?.status < 500) {
          return false;
        }
        // Retry up to 2 times for other errors
        return failureCount < 2;
      },
    },
  },
};

// Create query client instance
export const queryClient = new QueryClient(queryConfig);

// Query keys factory for consistent key management
export const queryKeys = {
  // Auth queries
  auth: {
    profile: ['auth', 'profile'] as const,
  },
  
  // User queries
  user: {
    profile: ['user', 'profile'] as const,
    stats: ['user', 'stats'] as const,
  },
  
  // Listing queries
  listings: {
    all: ['listings'] as const,
    search: (params: Record<string, any>) => ['listings', 'search', params] as const,
    detail: (id: string) => ['listings', 'detail', id] as const,
    myListings: ['listings', 'my'] as const,
  },
  
  // Swipe queries
  swipes: {
    mySwipes: ['swipes', 'my'] as const,
    stats: ['swipes', 'stats'] as const,
  },
  
  // Match queries
  matches: {
    myMatches: ['matches', 'my'] as const,
    active: ['matches', 'active'] as const,
    detail: (id: string) => ['matches', 'detail', id] as const,
    stats: ['matches', 'stats'] as const,
  },
  
  // Rating queries
  ratings: {
    house: (listingId: string) => ['ratings', 'house', listingId] as const,
    roommate: (userId: string) => ['ratings', 'roommate', userId] as const,
    landlord: (landlordId: string) => ['ratings', 'landlord', landlordId] as const,
  },
  
  // Billing queries
  billing: {
    status: ['billing', 'status'] as const,
    payment: (paymentId: string) => ['billing', 'payment', paymentId] as const,
  },
} as const;

// Utility functions for query management
export const queryUtils = {
  // Invalidate all queries
  invalidateAll: () => {
    queryClient.invalidateQueries();
  },
  
  // Invalidate specific query
  invalidateQuery: (queryKey: readonly unknown[]) => {
    queryClient.invalidateQueries({ queryKey });
  },
  
  // Remove specific query from cache
  removeQuery: (queryKey: readonly unknown[]) => {
    queryClient.removeQueries({ queryKey });
  },
  
  // Clear all queries
  clearAll: () => {
    queryClient.clear();
  },
  
  // Prefetch query
  prefetchQuery: async <T>(
    queryKey: readonly unknown[],
    queryFn: () => Promise<T>,
    options?: { staleTime?: number }
  ) => {
    await queryClient.prefetchQuery({
      queryKey,
      queryFn,
      staleTime: options?.staleTime,
    });
  },
  
  // Set query data
  setQueryData: <T>(queryKey: readonly unknown[], data: T) => {
    queryClient.setQueryData(queryKey, data);
  },
  
  // Get query data
  getQueryData: <T>(queryKey: readonly unknown[]): T | undefined => {
    return queryClient.getQueryData<T>(queryKey);
  },
};

// React Query DevTools (only in development)
export const enableDevTools = () => {
  if (__DEV__) {
    // You can import and use React Query DevTools here
    // import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
    // return <ReactQueryDevtools initialIsOpen={false} />;
  }
  return null;
};

export default queryClient;

