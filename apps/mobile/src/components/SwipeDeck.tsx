/**
 * SwipeDeck Component - Tinder-style swipe interface
 * Handles gesture-based swiping for property listings
 */

import React, { useCallback } from 'react';
import {
  View,
  StyleSheet,
  Dimensions,
  Text,
  PanResponder,
  Animated,
} from 'react-native';
import { theme } from '../theme';
import { useSwipe } from '../hooks/useSwipe';
import { useAuthStore } from '../store/auth';
import ListingCard from './ListingCard';
import type { ListingCardDTO } from '../types/dto';

interface SwipeDeckProps {
  listings: ListingCardDTO[];
  onMatch?: (listingId: string, matchId: string) => void;
  onSwipeLimitReached?: () => void;
}

const { width: screenWidth } = Dimensions.get('window');
const SWIPE_THRESHOLD = screenWidth * 0.25; // 25% of screen width
const ROTATION_MULTIPLIER = 0.1; // Rotation based on pan distance

export default function SwipeDeck({ 
  listings, 
  onMatch, 
  onSwipeLimitReached 
}: SwipeDeckProps) {
  const swipeMutation = useSwipe();
  const { hasUnlimitedSwipes } = useAuthStore();
  
  const [currentIndex, setCurrentIndex] = React.useState(0);
  const [swipeCount, setSwipeCount] = React.useState(0);
  const position = React.useRef(new Animated.ValueXY()).current;
  const rotate = React.useRef(new Animated.Value(0)).current;
  
  const MAX_FREE_SWIPES = 5; // Free users get 5 swipes
  
  const canSwipe = hasUnlimitedSwipes() || swipeCount < MAX_FREE_SWIPES;
  const currentListing = listings[currentIndex];
  
  const handleSwipeComplete = useCallback(async (direction: 'left' | 'right') => {
    if (!currentListing || !canSwipe) return;
    
    const action = direction === 'right' ? 'LIKE' : 'PASS';
    
    try {
      const result = await swipeMutation.mutateAsync({
        targetType: 'LISTING',
        targetId: currentListing.id,
        action,
      });
      
      // Handle match
      if (result.matchId && onMatch) {
        onMatch(currentListing.id, result.matchId);
      }
      
      // Move to next card
      setCurrentIndex(prev => prev + 1);
      setSwipeCount(prev => prev + 1);
      
      // Reset position for next card
      position.setValue({ x: 0, y: 0 });
      rotate.setValue(0);
      
      // Check if user hit swipe limit
      if (!hasUnlimitedSwipes() && swipeCount + 1 >= MAX_FREE_SWIPES) {
        onSwipeLimitReached?.();
      }
      
    } catch (error) {
      console.error('Swipe failed:', error);
      // Reset position on error
      Animated.spring(position, {
        toValue: { x: 0, y: 0 },
        useNativeDriver: false,
      }).start();
      rotate.setValue(0);
    }
  }, [currentListing, canSwipe, swipeMutation, onMatch, onSwipeLimitReached, hasUnlimitedSwipes, swipeCount, position, rotate]);
  
  const panResponder = React.useMemo(
    () =>
      PanResponder.create({
        onStartShouldSetPanResponder: () => canSwipe,
        onMoveShouldSetPanResponder: () => canSwipe,
        onPanResponderGrant: () => {
          // Add haptic feedback here if needed
        },
        onPanResponderMove: (_, gesture) => {
          if (!canSwipe) return;
          
          position.setValue({ x: gesture.dx, y: gesture.dy });
          
          // Calculate rotation based on horizontal movement
          const rotateValue = gesture.dx * ROTATION_MULTIPLIER;
          rotate.setValue(rotateValue);
        },
        onPanResponderRelease: (_, gesture) => {
          if (!canSwipe) return;
          
          const { dx, vx } = gesture;
          const shouldSwipeRight = dx > SWIPE_THRESHOLD || vx > 0.5;
          const shouldSwipeLeft = dx < -SWIPE_THRESHOLD || vx < -0.5;
          
          if (shouldSwipeRight) {
            // Swipe right (LIKE)
            Animated.parallel([
              Animated.timing(position, {
                toValue: { x: screenWidth * 1.5, y: 0 },
                duration: 200,
                useNativeDriver: false,
              }),
              Animated.timing(rotate, {
                toValue: 30,
                duration: 200,
                useNativeDriver: false,
              }),
            ]).start(() => handleSwipeComplete('right'));
          } else if (shouldSwipeLeft) {
            // Swipe left (PASS)
            Animated.parallel([
              Animated.timing(position, {
                toValue: { x: -screenWidth * 1.5, y: 0 },
                duration: 200,
                useNativeDriver: false,
              }),
              Animated.timing(rotate, {
                toValue: -30,
                duration: 200,
                useNativeDriver: false,
              }),
            ]).start(() => handleSwipeComplete('left'));
          } else {
            // Snap back to center
            Animated.parallel([
              Animated.spring(position, {
                toValue: { x: 0, y: 0 },
                useNativeDriver: false,
              }),
              Animated.spring(rotate, {
                toValue: 0,
                useNativeDriver: false,
              }),
            ]).start();
          }
        },
      }),
    [canSwipe, position, rotate, handleSwipeComplete]
  );
  
  // Show empty state if no listings
  if (!currentListing) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyIcon}>🏠</Text>
        <Text style={styles.emptyTitle}>No more properties</Text>
        <Text style={styles.emptySubtitle}>
          Check back later for new listings!
        </Text>
      </View>
    );
  }
  
  // Show swipe limit reached
  if (!canSwipe) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyIcon}>⏸️</Text>
        <Text style={styles.emptyTitle}>Swipe limit reached</Text>
        <Text style={styles.emptySubtitle}>
          Upgrade to Premium for unlimited swipes!
        </Text>
      </View>
    );
  }
  
  const rotateInterpolate = rotate.interpolate({
    inputRange: [-50, 0, 50],
    outputRange: ['-30deg', '0deg', '30deg'],
  });
  
  const likeOpacity = position.x.interpolate({
    inputRange: [0, SWIPE_THRESHOLD],
    outputRange: [0, 1],
    extrapolate: 'clamp',
  });
  
  const passOpacity = position.x.interpolate({
    inputRange: [-SWIPE_THRESHOLD, 0],
    outputRange: [1, 0],
    extrapolate: 'clamp',
  });
  
  return (
    <View style={styles.container}>
      {/* Next cards (stack preview) */}
      {listings.slice(currentIndex + 1, currentIndex + 3).map((listing, index) => (
        <View
          key={listing.id}
          style={[
            styles.card,
            {
              zIndex: -index - 1,
              transform: [
                { scale: 1 - (index + 1) * 0.05 },
                { translateY: (index + 1) * 10 },
              ],
            },
          ]}
        >
          <ListingCard listing={listing} />
        </View>
      ))}
      
      {/* Current card */}
      <Animated.View
        style={[
          styles.card,
          {
            transform: [
              { translateX: position.x },
              { translateY: position.y },
              { rotate: rotateInterpolate },
            ],
            zIndex: 1,
          },
        ]}
        {...panResponder.panHandlers}
      >
        <ListingCard listing={currentListing} />
        
        {/* Swipe indicators */}
        <Animated.View style={[styles.likeIndicator, { opacity: likeOpacity }]}>
          <Text style={styles.indicatorText}>INTERESTED</Text>
        </Animated.View>
        
        <Animated.View style={[styles.passIndicator, { opacity: passOpacity }]}>
          <Text style={styles.indicatorText}>PASS</Text>
        </Animated.View>
      </Animated.View>
      
      {/* Swipe count indicator */}
      {!hasUnlimitedSwipes() && (
        <View style={styles.swipeCounter}>
          <Text style={styles.swipeCounterText}>
            {MAX_FREE_SWIPES - swipeCount} swipes left
          </Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  card: {
    position: 'absolute',
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: theme.spacing.lg,
  },
  emptyIcon: {
    fontSize: 64,
    marginBottom: theme.spacing.lg,
  },
  emptyTitle: {
    fontSize: theme.typography.fontSize.xl,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.text,
    textAlign: 'center',
    marginBottom: theme.spacing.sm,
  },
  emptySubtitle: {
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.textSecondary,
    textAlign: 'center',
  },
  likeIndicator: {
    position: 'absolute',
    top: 100,
    left: 20,
    backgroundColor: theme.colors.like,
    paddingHorizontal: theme.spacing.lg,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.md,
    borderWidth: 2,
    borderColor: theme.colors.textInverse,
    transform: [{ rotate: '-20deg' }],
  },
  passIndicator: {
    position: 'absolute',
    top: 100,
    right: 20,
    backgroundColor: theme.colors.pass,
    paddingHorizontal: theme.spacing.lg,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.md,
    borderWidth: 2,
    borderColor: theme.colors.textInverse,
    transform: [{ rotate: '20deg' }],
  },
  indicatorText: {
    fontSize: theme.typography.fontSize.lg,
    fontWeight: theme.typography.fontWeight.bold,
    color: theme.colors.textInverse,
  },
  swipeCounter: {
    position: 'absolute',
    bottom: 20,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.xl,
  },
  swipeCounterText: {
    fontSize: theme.typography.fontSize.sm,
    color: theme.colors.textInverse,
    fontWeight: theme.typography.fontWeight.medium,
  },
});