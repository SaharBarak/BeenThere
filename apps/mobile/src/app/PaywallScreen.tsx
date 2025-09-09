import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  StatusBar,
  TouchableOpacity,
  Image,
  Alert,
  Linking,
  ActivityIndicator,
} from 'react-native';
import { api } from '../lib/api';
import { theme } from '../theme';

interface SubscriptionStatus {
  status: 'active' | 'inactive' | 'cancelled' | 'past_due';
  currentPeriodEnd?: string;
  cancelAtPeriodEnd?: boolean;
}

export default function PaywallScreen() {
  // TODO: Get user from auth store when implemented
  // const { user } = useAuthStore();
  const [subscriptionStatus, setSubscriptionStatus] = useState<SubscriptionStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    fetchSubscriptionStatus();
  }, []);

  const fetchSubscriptionStatus = async () => {
    try {
      setLoading(true);
      const response = await api.get('/billing/status');
      setSubscriptionStatus(response.data.data);
    } catch (error) {
      console.error('Failed to fetch subscription status:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubscribe = async () => {
    try {
      setProcessing(true);
      const response = await api.post('/billing/checkout', {
        priceId: 'price_monthly_20', // This would come from your billing configuration
        successUrl: 'beenthere://paywall/success',
        cancelUrl: 'beenthere://paywall/cancel',
      });

      const checkoutUrl = response.data.data.checkoutUrl;
      
      // Open the checkout URL in the default browser
      const supported = await Linking.canOpenURL(checkoutUrl);
      if (supported) {
        await Linking.openURL(checkoutUrl);
      } else {
        Alert.alert('Error', 'Cannot open checkout URL');
      }
    } catch (error) {
      console.error('Failed to create checkout session:', error);
      Alert.alert('Error', 'Failed to start checkout process');
    } finally {
      setProcessing(false);
    }
  };

  const handleRestoreSubscription = async () => {
    try {
      setProcessing(true);
      // This would typically involve checking with the app store for existing subscriptions
      Alert.alert('Restore Subscription', 'This feature would restore your existing subscription from the app store.');
    } catch (error) {
      console.error('Failed to restore subscription:', error);
      Alert.alert('Error', 'Failed to restore subscription');
    } finally {
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="light-content" />
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#fff" />
          <Text style={styles.loadingText}>Loading...</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Unlock Premium</Text>
        <Text style={styles.headerSubtitle}>
          Get unlimited access to all features
        </Text>
      </View>

      {/* Features List */}
      <View style={styles.featuresContainer}>
        <View style={styles.featureItem}>
          <View style={styles.featureIcon}>
            <Text style={styles.featureIconText}>🏠</Text>
          </View>
          <View style={styles.featureContent}>
            <Text style={styles.featureTitle}>Unlimited Swipes</Text>
            <Text style={styles.featureDescription}>
              Swipe through unlimited listings without restrictions
            </Text>
          </View>
        </View>

        <View style={styles.featureItem}>
          <View style={styles.featureIcon}>
            <Text style={styles.featureIconText}>💬</Text>
          </View>
          <View style={styles.featureContent}>
            <Text style={styles.featureTitle}>Priority Messaging</Text>
            <Text style={styles.featureDescription}>
              Get priority in landlord responses and faster matches
            </Text>
          </View>
        </View>

        <View style={styles.featureItem}>
          <View style={styles.featureIcon}>
            <Text style={styles.featureIconText}>⭐</Text>
          </View>
          <View style={styles.featureContent}>
            <Text style={styles.featureTitle}>Advanced Filters</Text>
            <Text style={styles.featureDescription}>
              Filter by price range, amenities, and location preferences
            </Text>
          </View>
        </View>

        <View style={styles.featureItem}>
          <View style={styles.featureIcon}>
            <Text style={styles.featureIconText}>🔒</Text>
          </View>
          <View style={styles.featureContent}>
            <Text style={styles.featureTitle}>Verified Listings</Text>
            <Text style={styles.featureDescription}>
              Access to verified and premium listings only
            </Text>
          </View>
        </View>
      </View>

      {/* Pricing */}
      <View style={styles.pricingContainer}>
        <View style={styles.priceCard}>
          <Text style={styles.priceAmount}>₪20</Text>
          <Text style={styles.pricePeriod}>per month</Text>
          <Text style={styles.priceDescription}>
            Cancel anytime. No hidden fees.
          </Text>
        </View>
      </View>

      {/* Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.subscribeButton, processing && styles.subscribeButtonDisabled]}
          onPress={handleSubscribe}
          disabled={processing}
        >
          {processing ? (
            <ActivityIndicator size="small" color="#fff" />
          ) : (
            <Text style={styles.subscribeButtonText}>
              Continue (₪20 / month)
            </Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.restoreButton}
          onPress={handleRestoreSubscription}
          disabled={processing}
        >
          <Text style={styles.restoreButtonText}>Restore Subscription</Text>
        </TouchableOpacity>
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>
          By subscribing, you agree to our Terms of Service and Privacy Policy.
          Subscription automatically renews unless cancelled.
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.primary,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    color: '#fff',
    fontSize: 16,
    marginTop: 16,
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 40,
    paddingBottom: 32,
    alignItems: 'center',
  },
  headerTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  headerSubtitle: {
    fontSize: 18,
    color: '#ccc',
    textAlign: 'center',
  },
  featuresContainer: {
    paddingHorizontal: 24,
    marginBottom: 32,
  },
  featureItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 24,
  },
  featureIcon: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#333',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  featureIconText: {
    fontSize: 24,
  },
  featureContent: {
    flex: 1,
  },
  featureTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#fff',
    marginBottom: 4,
  },
  featureDescription: {
    fontSize: 14,
    color: '#ccc',
    lineHeight: 20,
  },
  pricingContainer: {
    paddingHorizontal: 24,
    marginBottom: 32,
    alignItems: 'center',
  },
  priceCard: {
    backgroundColor: '#333',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    minWidth: 200,
  },
  priceAmount: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 4,
  },
  pricePeriod: {
    fontSize: 16,
    color: '#ccc',
    marginBottom: 8,
  },
  priceDescription: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
  buttonContainer: {
    paddingHorizontal: 24,
    marginBottom: 32,
  },
  subscribeButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    marginBottom: 16,
  },
  subscribeButtonDisabled: {
    backgroundColor: '#555',
  },
  subscribeButtonText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#fff',
  },
  restoreButton: {
    paddingVertical: 16,
    alignItems: 'center',
  },
  restoreButtonText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '500',
  },
  footer: {
    paddingHorizontal: 24,
    paddingBottom: 32,
  },
  footerText: {
    fontSize: 12,
    color: '#999',
    textAlign: 'center',
    lineHeight: 18,
  },
});

