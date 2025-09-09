/**
 * Ratings Screen - Rate houses, roommates, and landlords
 * Interactive rating interface with form validation
 */

import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  StatusBar,
  TouchableOpacity,
  ScrollView,
  TextInput,
  Alert,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { theme } from '../theme';
import { useSubmitRating, useUserRatings } from '../hooks/useRatings';
import { getRatingCategories } from '../validators/rating';
import type { RatingTargetType } from '../validators/rating';
import StarRating from '../components/StarRating';
import EmptyState from '../components/EmptyState';

export default function RatingsScreen() {
  const [activeTab, setActiveTab] = useState<RatingTargetType>('HOUSE');
  const [isRating, setIsRating] = useState(false);
  const [ratingForm, setRatingForm] = useState<{
    targetId: string;
    scores: Record<string, number>;
    comment: string;
  }>({
    targetId: '',
    scores: {},
    comment: '',
  });

  const submitRatingMutation = useSubmitRating();
  const { data: userRatings, isLoading: ratingsLoading } = useUserRatings();

  const resetRatingForm = () => {
    setRatingForm({
      targetId: '',
      scores: {},
      comment: '',
    });
    setIsRating(false);
  };

  const handleScoreChange = (category: string, score: number) => {
    setRatingForm(prev => ({
      ...prev,
      scores: {
        ...prev.scores,
        [category]: score,
      },
    }));
  };

  const handleSubmitRating = async () => {
    const categories = getRatingCategories(activeTab);
    const categoryKeys = Object.keys(categories);
    
    // Validate all scores are provided
    const missingScores = categoryKeys.filter(key => !ratingForm.scores[key]);
    if (missingScores.length > 0) {
      Alert.alert(
        'Incomplete Rating', 
        `Please rate all categories: ${missingScores.map(key => categories[key as keyof typeof categories]).join(', ')}`
      );
      return;
    }

    if (!ratingForm.targetId.trim()) {
      Alert.alert('Missing Information', 'Please enter a target ID to rate');
      return;
    }

    try {
      await submitRatingMutation.mutateAsync({
        targetType: activeTab,
        targetId: ratingForm.targetId,
        scores: ratingForm.scores,
        comment: ratingForm.comment || undefined,
      } as any); // Type assertion needed due to discriminated union complexity

      Alert.alert('Success!', 'Your rating has been submitted', [
        { text: 'OK', onPress: resetRatingForm }
      ]);
    } catch (error) {
      Alert.alert('Error', 'Failed to submit rating. Please try again.');
    }
  };

  const renderRatingForm = () => {
    const categories = getRatingCategories(activeTab);
    
    return (
      <ScrollView style={styles.form} showsVerticalScrollIndicator={false}>
        {/* Target ID Input */}
        <View style={styles.inputGroup}>
          <Text style={styles.inputLabel}>
            {activeTab === 'HOUSE' ? 'Property' : activeTab === 'ROOMMATE' ? 'Roommate' : 'Landlord'} ID
          </Text>
          <TextInput
            style={styles.textInput}
            value={ratingForm.targetId}
            onChangeText={(text) => setRatingForm(prev => ({ ...prev, targetId: text }))}
            placeholder={`Enter ${activeTab.toLowerCase()} ID`}
            placeholderTextColor={theme.colors.textTertiary}
          />
        </View>

        {/* Rating Categories */}
        {Object.entries(categories).map(([key, label]) => (
          <View key={key} style={styles.ratingGroup}>
            <StarRating
              label={label}
              rating={ratingForm.scores[key] || 0}
              onRatingChange={(rating) => handleScoreChange(key, rating)}
            />
          </View>
        ))}

        {/* Comment Input */}
        <View style={styles.inputGroup}>
          <Text style={styles.inputLabel}>Additional Comments (Optional)</Text>
          <TextInput
            style={[styles.textInput, styles.commentInput]}
            value={ratingForm.comment}
            onChangeText={(text) => setRatingForm(prev => ({ ...prev, comment: text }))}
            placeholder="Share your experience..."
            placeholderTextColor={theme.colors.textTertiary}
            multiline
            numberOfLines={4}
            textAlignVertical="top"
          />
        </View>

        {/* Submit Button */}
        <TouchableOpacity
          style={[
            styles.submitButton,
            submitRatingMutation.isPending && styles.submitButtonDisabled
          ]}
          onPress={handleSubmitRating}
          disabled={submitRatingMutation.isPending}
        >
          <Text style={styles.submitButtonText}>
            {submitRatingMutation.isPending ? 'Submitting...' : 'Submit Rating'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.cancelButton}
          onPress={resetRatingForm}
        >
          <Text style={styles.cancelButtonText}>Cancel</Text>
        </TouchableOpacity>
      </ScrollView>
    );
  };

  const renderTabContent = () => {
    if (isRating) {
      return renderRatingForm();
    }

    if (ratingsLoading) {
      return (
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>Loading your ratings...</Text>
        </View>
      );
    }

    const filteredRatings = userRatings?.filter(r => r.targetType === activeTab) || [];
    
    if (filteredRatings.length === 0) {
      const emptyStates = {
        HOUSE: {
          icon: '🏠',
          title: 'No house ratings yet',
          subtitle: 'Rate properties you\'ve lived at to help future tenants make informed decisions',
        },
        ROOMMATE: {
          icon: '👥',
          title: 'No roommate ratings yet',
          subtitle: 'Rate past roommates to build community trust and help others find compatible housemates',
        },
        LANDLORD: {
          icon: '🏢',
          title: 'No landlord ratings yet',
          subtitle: 'Rate landlords to help others make informed rental decisions',
        },
      };

      const content = emptyStates[activeTab];

      return (
        <EmptyState
          icon={content.icon}
          title={content.title}
          subtitle={content.subtitle}
          actionText={`Rate a ${activeTab.toLowerCase()}`}
          onAction={() => setIsRating(true)}
        />
      );
    }

    // Show existing ratings
    return (
      <ScrollView style={styles.ratingsList} showsVerticalScrollIndicator={false}>
        {filteredRatings.map((rating) => (
          <View key={rating.id} style={styles.ratingCard}>
            <Text style={styles.ratingCardTitle}>
              {activeTab} Rating
            </Text>
            <Text style={styles.ratingCardDate}>
              {new Date(rating.createdAt).toLocaleDateString()}
            </Text>
            {rating.comment && (
              <Text style={styles.ratingCardComment}>"{rating.comment}"</Text>
            )}
          </View>
        ))}
        
        <TouchableOpacity
          style={styles.addButton}
          onPress={() => setIsRating(true)}
        >
          <Text style={styles.addButtonText}>+ Add New Rating</Text>
        </TouchableOpacity>
      </ScrollView>
    );
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="dark-content" backgroundColor={theme.colors.background} />
        
        {/* Header */}
        <View style={styles.header}>
          <View style={styles.headerContent}>
            <Text style={styles.title}>Ratings</Text>
            <Text style={styles.subtitle}>
              {isRating ? 'Rate your experience' : 'Build trust in the community'}
            </Text>
          </View>
          
          {isRating && (
            <TouchableOpacity
              style={styles.closeButton}
              onPress={resetRatingForm}
            >
              <Text style={styles.closeButtonText}>✕</Text>
            </TouchableOpacity>
          )}
        </View>

        {/* Segmented Control */}
        {!isRating && (
          <View style={styles.segmentedControl}>
            {(['HOUSE', 'ROOMMATE', 'LANDLORD'] as RatingTargetType[]).map((tab) => (
              <TouchableOpacity
                key={tab}
                style={[
                  styles.segmentButton,
                  activeTab === tab && styles.segmentButtonActive,
                ]}
                onPress={() => setActiveTab(tab)}
              >
                <Text
                  style={[
                    styles.segmentButtonText,
                    activeTab === tab && styles.segmentButtonTextActive,
                  ]}
                >
                  {tab.charAt(0) + tab.slice(1).toLowerCase() + 's'}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        )}

        {/* Content */}
        <View style={styles.content}>
          {renderTabContent()}
        </View>
      </SafeAreaView>
    </KeyboardAvoidingView>
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
  closeButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: theme.colors.backgroundSecondary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  closeButtonText: {
    fontSize: theme.typography.fontSize.lg,
    color: theme.colors.textSecondary,
    fontWeight: theme.typography.fontWeight.semiBold,
  },
  segmentedControl: {
    flexDirection: 'row',
    backgroundColor: theme.colors.backgroundSecondary,
    margin: theme.spacing.md,
    borderRadius: theme.borderRadius.lg,
    padding: theme.spacing.xs,
  },
  segmentButton: {
    flex: 1,
    paddingVertical: theme.spacing.sm,
    alignItems: 'center',
    borderRadius: theme.borderRadius.md,
  },
  segmentButtonActive: {
    backgroundColor: theme.colors.surface,
    ...theme.shadows.sm,
  },
  segmentButtonText: {
    fontSize: theme.typography.fontSize.sm,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.textSecondary,
  },
  segmentButtonTextActive: {
    color: theme.colors.text,
    fontWeight: theme.typography.fontWeight.semiBold,
  },
  content: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.textSecondary,
  },
  // Rating Form Styles
  form: {
    flex: 1,
    paddingHorizontal: theme.spacing.lg,
  },
  inputGroup: {
    marginBottom: theme.spacing.lg,
  },
  inputLabel: {
    fontSize: theme.typography.fontSize.base,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.text,
    marginBottom: theme.spacing.sm,
  },
  textInput: {
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.md,
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.text,
  },
  commentInput: {
    height: 100,
    textAlignVertical: 'top',
  },
  ratingGroup: {
    marginBottom: theme.spacing.lg,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.sm,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.lg,
    ...theme.shadows.sm,
  },
  submitButton: {
    backgroundColor: theme.colors.primary,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.lg,
    borderRadius: theme.borderRadius.lg,
    alignItems: 'center',
    marginBottom: theme.spacing.md,
    ...theme.shadows.md,
  },
  submitButtonDisabled: {
    backgroundColor: theme.colors.textTertiary,
  },
  submitButtonText: {
    fontSize: theme.typography.fontSize.lg,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.textInverse,
  },
  cancelButton: {
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.lg,
    alignItems: 'center',
    marginBottom: theme.spacing.xl,
  },
  cancelButtonText: {
    fontSize: theme.typography.fontSize.base,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.textSecondary,
  },
  // Ratings List Styles
  ratingsList: {
    flex: 1,
    paddingHorizontal: theme.spacing.md,
  },
  ratingCard: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.lg,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.md,
    ...theme.shadows.sm,
  },
  ratingCardTitle: {
    fontSize: theme.typography.fontSize.lg,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
  },
  ratingCardDate: {
    fontSize: theme.typography.fontSize.sm,
    color: theme.colors.textSecondary,
    marginBottom: theme.spacing.sm,
  },
  ratingCardComment: {
    fontSize: theme.typography.fontSize.base,
    color: theme.colors.text,
    fontStyle: 'italic',
  },
  addButton: {
    backgroundColor: theme.colors.backgroundSecondary,
    borderWidth: 2,
    borderColor: theme.colors.border,
    borderStyle: 'dashed',
    borderRadius: theme.borderRadius.lg,
    paddingVertical: theme.spacing.lg,
    alignItems: 'center',
    marginBottom: theme.spacing.xl,
  },
  addButtonText: {
    fontSize: theme.typography.fontSize.lg,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.textSecondary,
  },
});