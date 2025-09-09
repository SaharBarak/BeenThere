/**
 * Environment configuration for BeenThere mobile app
 * Reads API base URL from environment variables
 */

export const baseUrl = process.env.EXPO_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

export const env = {
  API_BASE_URL: baseUrl,
  isDevelopment: __DEV__,
} as const;