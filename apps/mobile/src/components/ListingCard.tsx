/**
 * ListingCard Component - Property display for BeenThere
 * Tinder-style card showing apartment/roommate listings
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  ImageBackground,
} from 'react-native';
import { theme } from '../theme';
import type { ListingCardDTO } from '../types/dto';

interface ListingCardProps {
  listing: ListingCardDTO;
  style?: any;
}

const { width: screenWidth, height: screenHeight } = Dimensions.get('window');
const CARD_HEIGHT = screenHeight * 0.75; // 75% of screen height
const CARD_WIDTH = screenWidth * 0.9; // 90% of screen width

export default function ListingCard({ listing, style }: ListingCardProps) {
  // Mock image for now - will be real property photos in future
  const mockImageUrl = `https://picsum.photos/800/600?random=${listing.id}`;
  
  const isEntirePlace = listing.type === 'ENTIRE_PLACE';
  
  return (
    <View style={[styles.card, style]}>
      <ImageBackground
        source={{ uri: mockImageUrl }}
        style={styles.imageBackground}
        imageStyle={styles.image}
      >
        {/* Gradient overlay for text readability */}
        <View style={styles.gradientOverlay} />
        
        {/* Type badge */}
        <View style={styles.typeBadge}>
          <Text style={styles.typeBadgeText}>
            {isEntirePlace ? '🏠 Entire Place' : '👥 Roommates'}
          </Text>
        </View>
        
        {/* Main content at bottom */}
        <View style={styles.content}>
          <View style={styles.mainInfo}>
            <Text style={styles.title} numberOfLines={2}>
              {listing.title}
            </Text>
            
            <View style={styles.locationRow}>
              <Text style={styles.location}>📍 {listing.city}</Text>
            </View>
            
            <View style={styles.priceRow}>
              <Text style={styles.price}>
                ₪{listing.price.toLocaleString()}
              </Text>
              <Text style={styles.priceUnit}>
                {isEntirePlace ? '/month' : '/month per person'}
              </Text>
            </View>
          </View>
          
          {/* Quick info chips */}
          <View style={styles.chipsContainer}>
            <View style={styles.chip}>
              <Text style={styles.chipText}>Available Now</Text>
            </View>
            <View style={styles.chip}>
              <Text style={styles.chipText}>
                {isEntirePlace ? 'Full Apartment' : 'Looking for Roommate'}
              </Text>
            </View>
          </View>
        </View>
      </ImageBackground>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    width: CARD_WIDTH,
    height: CARD_HEIGHT,
    borderRadius: theme.borderRadius.xl,
    backgroundColor: theme.colors.surface,
    ...theme.shadows.lg,
    overflow: 'hidden',
  },
  imageBackground: {
    flex: 1,
    justifyContent: 'space-between',
  },
  image: {
    borderRadius: theme.borderRadius.xl,
  },
  gradientOverlay: {
    ...StyleSheet.absoluteFillObject,
    // For React Native, we'll use a semi-transparent overlay
    // In the future, we can use react-native-linear-gradient for better gradients
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  typeBadge: {
    position: 'absolute',
    top: theme.spacing.lg,
    right: theme.spacing.lg,
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.xl,
  },
  typeBadgeText: {
    fontSize: theme.typography.fontSize.sm,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.text,
  },
  content: {
    padding: theme.spacing.lg,
    paddingTop: theme.spacing.xl,
  },
  mainInfo: {
    marginBottom: theme.spacing.md,
  },
  title: {
    fontSize: theme.typography.fontSize['2xl'],
    fontWeight: theme.typography.fontWeight.bold,
    color: theme.colors.textInverse,
    marginBottom: theme.spacing.sm,
    lineHeight: theme.typography.lineHeight.tight * theme.typography.fontSize['2xl'],
  },
  locationRow: {
    marginBottom: theme.spacing.sm,
  },
  location: {
    fontSize: theme.typography.fontSize.base,
    color: 'rgba(255, 255, 255, 0.9)',
    fontWeight: theme.typography.fontWeight.medium,
  },
  priceRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  price: {
    fontSize: theme.typography.fontSize['3xl'],
    fontWeight: theme.typography.fontWeight.bold,
    color: theme.colors.textInverse,
    marginRight: theme.spacing.xs,
  },
  priceUnit: {
    fontSize: theme.typography.fontSize.base,
    color: 'rgba(255, 255, 255, 0.8)',
    fontWeight: theme.typography.fontWeight.medium,
  },
  chipsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: theme.spacing.sm,
  },
  chip: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.xs,
    borderRadius: theme.borderRadius.xl,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.3)',
  },
  chipText: {
    fontSize: theme.typography.fontSize.sm,
    color: theme.colors.textInverse,
    fontWeight: theme.typography.fontWeight.medium,
  },
});