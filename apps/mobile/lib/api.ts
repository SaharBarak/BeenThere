import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from './store';

// Base URL for the API
const BASE_URL = __DEV__ 
  ? 'http://localhost:8080/api' 
  : 'https://api.beenthere.app/api';

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 errors (unauthorized)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      // Clear auth state and redirect to login
      useAuthStore.getState().logout();
      
      // You might want to navigate to login screen here
      // navigationRef.current?.reset({
      //   index: 0,
      //   routes: [{ name: 'Login' }],
      // });
    }

    // Handle network errors
    if (!error.response) {
      error.message = 'Network error. Please check your connection.';
    }

    return Promise.reject(error);
  }
);

// API endpoints
export const api = {
  // Auth endpoints
  auth: {
    register: (data: RegisterRequest) => 
      apiClient.post('/auth/register', data),
    login: (data: LoginRequest) => 
      apiClient.post('/auth/login', data),
    refresh: () => 
      apiClient.post('/auth/refresh'),
  },

  // User endpoints
  user: {
    getProfile: () => 
      apiClient.get('/users/me'),
    updateProfile: (data: UpdateUserRequest) => 
      apiClient.put('/users/me', data),
    deleteProfile: () => 
      apiClient.delete('/users/me'),
  },

  // Listing endpoints
  listings: {
    getAll: (params?: ListingSearchParams) => 
      apiClient.get('/listings', { params }),
    search: (params: ListingSearchParams) => 
      apiClient.get('/listings/search', { params }),
    getById: (id: string) => 
      apiClient.get(`/listings/${id}`),
    getMyListings: () => 
      apiClient.get('/listings/my-listings'),
    create: (data: CreateListingRequest) => 
      apiClient.post('/listings', data),
    update: (id: string, data: UpdateListingRequest) => 
      apiClient.put(`/listings/${id}`, data),
    delete: (id: string) => 
      apiClient.delete(`/listings/${id}`),
  },

  // Swipe endpoints
  swipes: {
    create: (data: CreateSwipeRequest) => 
      apiClient.post('/swipes', data),
    getMySwipes: () => 
      apiClient.get('/swipes/my-swipes'),
    getStats: () => 
      apiClient.get('/swipes/stats'),
  },

  // Match endpoints
  matches: {
    getMyMatches: () => 
      apiClient.get('/matches/my-matches'),
    getActiveMatches: () => 
      apiClient.get('/matches/active'),
    getById: (id: string) => 
      apiClient.get(`/matches/${id}`),
    updateStatus: (id: string, data: UpdateMatchStatusRequest) => 
      apiClient.put(`/matches/${id}/status`, data),
    getStats: () => 
      apiClient.get('/matches/stats'),
  },

  // Rating endpoints
  ratings: {
    createHouseRating: (data: CreateHouseRatingRequest) => 
      apiClient.post('/ratings/houses', data),
    getHouseRatings: (listingId: string) => 
      apiClient.get(`/ratings/houses/listing/${listingId}`),
    createRoommateRating: (data: CreateRoommateRatingRequest) => 
      apiClient.post('/ratings/roommates', data),
    getRoommateRatings: (userId: string) => 
      apiClient.get(`/ratings/roommates/user/${userId}`),
    createLandlordRating: (data: CreateLandlordRatingRequest) => 
      apiClient.post('/ratings/landlords', data),
    getLandlordRatings: (landlordId: string) => 
      apiClient.get(`/ratings/landlords/${landlordId}`),
  },

  // Billing endpoints
  billing: {
    getStatus: () => 
      apiClient.get('/billing/status'),
    createPayment: (data: CreatePaymentRequest) => 
      apiClient.post('/billing/payments', data),
    refundPayment: (paymentId: string, data: RefundRequest) => 
      apiClient.post(`/billing/payments/${paymentId}/refund`, data),
    getPaymentStatus: (paymentId: string) => 
      apiClient.get(`/billing/payments/${paymentId}/status`),
    createCheckout: (data: CreateCheckoutRequest) => 
      apiClient.post('/billing/checkout', data),
  },
};

// Type definitions
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
  dateOfBirth?: string;
  bio?: string;
  profileImageUrl?: string;
}

export interface ListingSearchParams {
  city?: string;
  state?: string;
  minRent?: number;
  maxRent?: number;
  bedrooms?: number;
  propertyType?: string;
}

export interface CreateListingRequest {
  title: string;
  description: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  latitude: number;
  longitude: number;
  rentAmount: number;
  depositAmount?: number;
  bedrooms: number;
  bathrooms: number;
  squareFeet?: number;
  propertyType: string;
  furnished?: boolean;
  petFriendly?: boolean;
  smokingAllowed?: boolean;
  utilitiesIncluded?: boolean;
  parkingAvailable?: boolean;
  laundryAvailable?: boolean;
  gymAvailable?: boolean;
  poolAvailable?: boolean;
  availableDate: string;
  leaseDurationMonths?: number;
  images?: string[];
}

export interface UpdateListingRequest {
  title?: string;
  description?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  latitude?: number;
  longitude?: number;
  rentAmount?: number;
  depositAmount?: number;
  bedrooms?: number;
  bathrooms?: number;
  squareFeet?: number;
  propertyType?: string;
  furnished?: boolean;
  petFriendly?: boolean;
  smokingAllowed?: boolean;
  utilitiesIncluded?: boolean;
  parkingAvailable?: boolean;
  laundryAvailable?: boolean;
  gymAvailable?: boolean;
  poolAvailable?: boolean;
  availableDate?: string;
  leaseDurationMonths?: number;
  images?: string[];
}

export interface CreateSwipeRequest {
  listingId: string;
  swipeType: 'LIKE' | 'DISLIKE' | 'SUPER_LIKE';
}

export interface UpdateMatchStatusRequest {
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED';
}

export interface CreateHouseRatingRequest {
  listingId: string;
  rating: number;
  comment?: string;
  cleanlinessRating?: number;
  locationRating?: number;
  valueRating?: number;
  amenitiesRating?: number;
  noiseLevelRating?: number;
  safetyRating?: number;
}

export interface CreateRoommateRatingRequest {
  ratedUserId: string;
  rating: number;
  comment?: string;
  cleanlinessRating?: number;
  communicationRating?: number;
  respectRating?: number;
  reliabilityRating?: number;
  socialRating?: number;
}

export interface CreateLandlordRatingRequest {
  landlordId: string;
  rating: number;
  comment?: string;
  responsivenessRating?: number;
  maintenanceRating?: number;
  communicationRating?: number;
  fairnessRating?: number;
  professionalismRating?: number;
}

export interface CreatePaymentRequest {
  amount: number;
  currency?: string;
  description: string;
  metadata?: Record<string, string>;
}

export interface RefundRequest {
  amount: number;
}

export interface CreateCheckoutRequest {
  priceId: string;
  successUrl: string;
  cancelUrl: string;
}

export default apiClient;

