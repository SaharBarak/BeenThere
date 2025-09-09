/**
 * Swipe mutation hook using TanStack Query
 * Handles swipe actions and match detection
 */

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import { swipeRequestSchema, swipeResponseSchema } from '../validators/swipe';
import type { SwipeRequest, SwipeResponse } from '../types/dto';

const performSwipe = async (swipeData: SwipeRequest): Promise<SwipeResponse> => {
  // Validate request data
  const validatedRequest = swipeRequestSchema.parse(swipeData);
  
  const response = await api.post('/swipes', validatedRequest);
  
  // Validate response
  const validatedResponse = swipeResponseSchema.parse(response.data);
  return validatedResponse;
};

export const useSwipe = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: performSwipe,
    onSuccess: (data, variables) => {
      // Invalidate feed queries to refresh after swipe
      queryClient.invalidateQueries({ queryKey: ['feed'] });
      
      // If we got a match, invalidate matches queries
      if (data.matchId) {
        queryClient.invalidateQueries({ queryKey: ['matches'] });
      }
      
      console.log(`Swiped ${variables.action} on ${variables.targetType} ${variables.targetId}`);
      if (data.matchId) {
        console.log(`🎉 Match created: ${data.matchId}`);
      }
    },
    onError: (error) => {
      console.error('Swipe failed:', error);
    },
  });
};