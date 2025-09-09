/**
 * Zustand auth store for BeenThere
 * Manages user authentication and subscription status
 */

import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { UserProfileDTO, BillingStatus } from '../types/dto';

interface AuthState {
  // User data
  user: UserProfileDTO | null;
  isAuthenticated: boolean;
  
  // Subscription data  
  subscription: BillingStatus | null;
  
  // Loading states
  isLoading: boolean;
  
  // Actions
  setUser: (user: UserProfileDTO | null) => void;
  setSubscription: (subscription: BillingStatus | null) => void;
  setLoading: (loading: boolean) => void;
  logout: () => Promise<void>;
  
  // Computed properties
  isSubscriptionActive: () => boolean;
  hasUnlimitedSwipes: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  // Initial state
  user: null,
  isAuthenticated: false,
  subscription: null,
  isLoading: true,
  
  // Actions
  setUser: (user) => set({ 
    user, 
    isAuthenticated: !!user 
  }),
  
  setSubscription: (subscription) => set({ subscription }),
  
  setLoading: (isLoading) => set({ isLoading }),
  
  logout: async () => {
    try {
      // Clear stored tokens
      await AsyncStorage.multiRemove(['auth_token', 'refresh_token']);
      
      // Reset state
      set({
        user: null,
        isAuthenticated: false,
        subscription: null,
        isLoading: false,
      });
    } catch (error) {
      console.error('Logout error:', error);
    }
  },
  
  // Computed properties
  isSubscriptionActive: () => {
    const { subscription } = get();
    return subscription?.status === 'ACTIVE';
  },
  
  hasUnlimitedSwipes: () => {
    const { isSubscriptionActive } = get();
    return isSubscriptionActive();
  },
}));