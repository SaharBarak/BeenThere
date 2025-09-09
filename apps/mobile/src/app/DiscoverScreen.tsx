/**
 * Discover Screen - Main swipe interface for BeenThere
 * Tinder-style swiping for roommates and apartments
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  StatusBar,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { theme } from '../theme';
import { useFeedListings } from '../hooks/useFeed';
import { useBillingStatus } from '../hooks/useBilling';
import { useAuthStore } from '../store/auth';
import SwipeDeck from '../components/SwipeDeck';
import MatchModal from '../components/MatchModal';
import EmptyState from '../components/EmptyState';

export default function DiscoverScreen() {
  const navigation = useNavigation();
  const { listings, isLoading, error, refetch } = useFeedListings();
  const { data: billingStatus } = useBillingStatus();
  const { hasUnlimitedSwipes } = useAuthStore();
  
  // Match modal state
  const [matchModal, setMatchModal] = React.useState<{
    visible: boolean;
    propertyTitle: string;
    matchId: string;
  }>({
    visible: false,
    propertyTitle: '',
    matchId: '',
  });
  
  const handleMatch = (listingId: string, matchId: string) => {
    const matchedListing = listings.find(l => l.id === listingId);
    if (matchedListing) {
      setMatchModal({
        visible: true,
        propertyTitle: matchedListing.title,
        matchId,
      });
    }
  };
  
  const handleSwipeLimitReached = () => {
    // Navigate to paywall
    navigation.navigate('Paywall' as never);
  };
  
  const handleStartChat = () => {
    // Navigate to chat thread (will implement in future)
    console.log('Navigate to chat for match:', matchModal.matchId);
  };
  
  const closeMatchModal = () => {
    setMatchModal(prev => ({ ...prev, visible: false }));
  };
  
  if (isLoading) {
    return (
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="dark-content" backgroundColor={theme.colors.background} />
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
          <Text style={styles.loadingText}>Loading properties...</Text>
        </View>
      </SafeAreaView>
    );
  }
  
  if (error) {
    return (
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="dark-content" backgroundColor={theme.colors.background} />
        <EmptyState
          icon="📡"
          title="Connection Error"
          subtitle="Please check your internet connection and try again"
          actionText="Retry"
          onAction={refetch}
        />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" backgroundColor={theme.colors.background} />
      
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.headerContent}>
          <Text style={styles.title}>Discover</Text>
          <Text style={styles.subtitle}>Find your perfect housing match</Text>
        </View>
        
        {/* Subscription indicator */}
        {!hasUnlimitedSwipes() && (
          <TouchableOpacity 
            style={styles.premiumBadge}
            onPress={() => navigation.navigate('Paywall' as never)}
          >
            <Text style={styles.premiumBadgeText}>Go Premium</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Main SwipeDeck */}
      <View style={styles.content}>
        {listings.length > 0 ? (
          <SwipeDeck
            listings={listings}
            onMatch={handleMatch}
            onSwipeLimitReached={handleSwipeLimitReached}
          />
        ) : (
          <EmptyState
            icon="🏠"
            title="No properties available"
            subtitle="Check back later for new listings in your area!"
            actionText="Refresh"
            onAction={refetch}
          />
        )}
      </View>
      
      {/* Match Modal */}
      <MatchModal
        visible={matchModal.visible}
        propertyTitle={matchModal.propertyTitle}
        onClose={closeMatchModal}
        onStartChat={handleStartChat}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.md,
    paddingBottom: theme.spacing.lg,
    backgroundColor: theme.colors.surface,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.borderLight,
  },
  headerContent: {
    flex: 1,
  },
  title: {
    fontSize: theme.typography.fontSize['3xl'],
    fontWeight: theme.typography.fontWeight.bold,
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
  },
  subtitle: {
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.textSecondary,
  },
  premiumBadge: {
    backgroundColor: theme.colors.accent,
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.xl,
  },
  premiumBadgeText: {
    fontSize: theme.typography.fontSize.sm,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.textInverse,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.textSecondary,
    marginTop: theme.spacing.md,
  },
});