/**
 * StarRating Component - Interactive star rating with animations
 * Used for rating houses, roommates, and landlords
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
} from 'react-native';
import { theme } from '../theme';

interface StarRatingProps {
  rating: number;
  onRatingChange: (rating: number) => void;
  size?: number;
  readonly?: boolean;
  label?: string;
}

export default function StarRating({
  rating,
  onRatingChange,
  size = 32,
  readonly = false,
  label,
}: StarRatingProps) {
  const scaleAnims = React.useRef(
    Array.from({ length: 5 }, () => new Animated.Value(1))
  ).current;

  const handlePress = (index: number) => {
    if (readonly) return;
    
    const newRating = index + 1;
    onRatingChange(newRating);
    
    // Animate the pressed star
    Animated.sequence([
      Animated.timing(scaleAnims[index], {
        toValue: 1.3,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(scaleAnims[index], {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }),
    ]).start();
  };

  const renderStar = (index: number) => {
    const isFilled = index < rating;
    const isHalfFilled = index === Math.floor(rating) && rating % 1 !== 0;
    
    return (
      <TouchableOpacity
        key={index}
        onPress={() => handlePress(index)}
        disabled={readonly}
        style={styles.starContainer}
        activeOpacity={0.7}
      >
        <Animated.View
          style={[
            styles.starWrapper,
            {
              transform: [{ scale: scaleAnims[index] }],
            },
          ]}
        >
          <Text
            style={[
              styles.star,
              {
                fontSize: size,
                color: isFilled || isHalfFilled 
                  ? theme.colors.accent 
                  : theme.colors.borderLight,
              },
            ]}
          >
            {isFilled ? '★' : isHalfFilled ? '☆' : '☆'}
          </Text>
        </Animated.View>
      </TouchableOpacity>
    );
  };

  return (
    <View style={styles.container}>
      {label && <Text style={styles.label}>{label}</Text>}
      <View style={styles.starsContainer}>
        {Array.from({ length: 5 }, (_, index) => renderStar(index))}
      </View>
      {!readonly && (
        <Text style={styles.ratingText}>
          {rating > 0 ? `${rating} star${rating !== 1 ? 's' : ''}` : 'Tap to rate'}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
  },
  label: {
    fontSize: theme.typography.fontSize.base,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.text,
    marginBottom: theme.spacing.sm,
    textAlign: 'center',
  },
  starsContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.xs,
  },
  starContainer: {
    marginHorizontal: theme.spacing.xs,
  },
  starWrapper: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  star: {
    textAlign: 'center',
  },
  ratingText: {
    fontSize: theme.typography.fontSize.sm,
    color: theme.colors.textSecondary,
    textAlign: 'center',
  },
});