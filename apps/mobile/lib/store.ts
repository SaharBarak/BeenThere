import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

// User interface
interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  dateOfBirth?: string;
  bio?: string;
  profileImageUrl?: string;
  isVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

// Subscription interface
interface Subscription {
  status: 'active' | 'inactive' | 'cancelled' | 'past_due';
  currentPeriodEnd?: string;
  cancelAtPeriodEnd?: boolean;
  planId?: string;
  priceId?: string;
}

// Auth state interface
interface AuthState {
  // State
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  login: (user: User, token: string) => void;
  logout: () => void;
  updateUser: (user: Partial<User>) => void;
  setLoading: (loading: boolean) => void;
}

// Subscription state interface
interface SubscriptionState {
  // State
  subscription: Subscription | null;
  isLoading: boolean;
  
  // Actions
  setSubscription: (subscription: Subscription | null) => void;
  setLoading: (loading: boolean) => void;
  checkSubscriptionStatus: () => Promise<void>;
}

// Create auth store
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      
      // Actions
      login: (user: User, token: string) => {
        set({
          user,
          token,
          isAuthenticated: true,
          isLoading: false,
        });
      },
      
      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
        });
        
        // Clear AsyncStorage
        AsyncStorage.multiRemove(['auth-storage', 'subscription-storage']);
      },
      
      updateUser: (userData: Partial<User>) => {
        const currentUser = get().user;
        if (currentUser) {
          set({
            user: { ...currentUser, ...userData },
          });
        }
      },
      
      setLoading: (loading: boolean) => {
        set({ isLoading: loading });
      },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

// Create subscription store
export const useSubscriptionStore = create<SubscriptionState>()(
  persist(
    (set, get) => ({
      // Initial state
      subscription: null,
      isLoading: false,
      
      // Actions
      setSubscription: (subscription: Subscription | null) => {
        set({ subscription });
      },
      
      setLoading: (loading: boolean) => {
        set({ isLoading: loading });
      },
      
      checkSubscriptionStatus: async () => {
        set({ isLoading: true });
        
        try {
          // This would typically make an API call to check subscription status
          // const response = await api.billing.getStatus();
          // set({ subscription: response.data.data });
          
          // For now, we'll simulate a check
          const currentSubscription = get().subscription;
          if (currentSubscription) {
            // Simulate checking if subscription is still active
            if (currentSubscription.currentPeriodEnd) {
              const endDate = new Date(currentSubscription.currentPeriodEnd);
              const now = new Date();
              
              if (now > endDate && currentSubscription.status === 'active') {
                set({ subscription: { ...currentSubscription, status: 'past_due' } });
              }
            }
          }
        } catch (error) {
          console.error('Failed to check subscription status:', error);
        } finally {
          set({ isLoading: false });
        }
      },
    }),
    {
      name: 'subscription-storage',
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (state) => ({
        subscription: state.subscription,
      }),
    }
  )
);

// Combined store for easy access
export const useAppStore = () => {
  const auth = useAuthStore();
  const subscription = useSubscriptionStore();
  
  return {
    auth,
    subscription,
    
    // Convenience getters
    get user() {
      return auth.user;
    },
    
    get isAuthenticated() {
      return auth.isAuthenticated;
    },
    
    get hasActiveSubscription() {
      return subscription.subscription?.status === 'active';
    },
    
    get needsSubscription() {
      return !subscription.subscription || subscription.subscription.status !== 'active';
    },
    
    // Combined actions
    logout: () => {
      auth.logout();
      subscription.setSubscription(null);
    },
  };
};

// Store selectors for performance optimization
export const authSelectors = {
  user: (state: AuthState) => state.user,
  token: (state: AuthState) => state.token,
  isAuthenticated: (state: AuthState) => state.isAuthenticated,
  isLoading: (state: AuthState) => state.isLoading,
};

export const subscriptionSelectors = {
  subscription: (state: SubscriptionState) => state.subscription,
  isLoading: (state: SubscriptionState) => state.isLoading,
  hasActiveSubscription: (state: SubscriptionState) => 
    state.subscription?.status === 'active',
  needsSubscription: (state: SubscriptionState) => 
    !state.subscription || state.subscription.status !== 'active',
};

// Store utilities
export const storeUtils = {
  // Clear all stores
  clearAll: () => {
    useAuthStore.getState().logout();
    useSubscriptionStore.getState().setSubscription(null);
  },
  
  // Get current auth state
  getAuthState: () => useAuthStore.getState(),
  
  // Get current subscription state
  getSubscriptionState: () => useSubscriptionStore.getState(),
  
  // Check if user needs to see paywall
  shouldShowPaywall: () => {
    const subscription = useSubscriptionStore.getState().subscription;
    return !subscription || subscription.status !== 'active';
  },
};

export default useAppStore;

