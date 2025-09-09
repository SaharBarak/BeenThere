/**
 * Ratings hooks for BeenThere
 * Handles rating submission and fetching
 */

import { useMutation, useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import { ratingRequestSchema, ratingResponseSchema } from '../validators/rating';
import type { RatingRequest, RatingResponse, RatingTargetType } from '../validators/rating';

// Submit a rating
const submitRating = async (ratingData: RatingRequest): Promise<RatingResponse> => {
  // Validate request data
  const validatedRequest = ratingRequestSchema.parse(ratingData);
  
  const response = await api.post('/ratings', validatedRequest);
  
  // Validate response
  const validatedResponse = ratingResponseSchema.parse(response.data);
  return validatedResponse;
};

export const useSubmitRating = () => {
  return useMutation({
    mutationFn: submitRating,
    onSuccess: (data) => {
      console.log('Rating submitted successfully:', data.id);
    },
    onError: (error) => {
      console.error('Rating submission failed:', error);
    },
  });
};

// Fetch ratings for a specific target
const fetchRatings = async (targetType: RatingTargetType, targetId: string): Promise<RatingResponse[]> => {
  const response = await api.get(`/ratings/${targetType.toLowerCase()}/${targetId}`);
  
  // Validate array of ratings
  const ratingsArray = Array.isArray(response.data) ? response.data : [];
  const validatedRatings = ratingsArray.map(rating => ratingResponseSchema.parse(rating));
  return validatedRatings;
};

export const useRatings = (targetType: RatingTargetType, targetId: string) => {
  return useQuery({
    queryKey: ['ratings', targetType, targetId],
    queryFn: () => fetchRatings(targetType, targetId),
    enabled: !!targetId,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};

// Get user's own ratings (what they've rated)
const fetchUserRatings = async (): Promise<RatingResponse[]> => {
  const response = await api.get('/ratings/me');
  
  const ratingsArray = Array.isArray(response.data) ? response.data : [];
  const validatedRatings = ratingsArray.map(rating => ratingResponseSchema.parse(rating));
  return validatedRatings;
};

export const useUserRatings = () => {
  return useQuery({
    queryKey: ['ratings', 'user'],
    queryFn: fetchUserRatings,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};