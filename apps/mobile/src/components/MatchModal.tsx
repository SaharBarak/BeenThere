/**
 * MatchModal Component - Celebration modal for new matches
 * Shows when users match with properties/roommates
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  Dimensions,
  Animated,
} from 'react-native';
import { theme } from '../theme';

interface MatchModalProps {
  visible: boolean;
  propertyTitle: string;
  onClose: () => void;
  onStartChat: () => void;
}

const { width: screenWidth, height: screenHeight } = Dimensions.get('window');

export default function MatchModal({ 
  visible, 
  propertyTitle, 
  onClose, 
  onStartChat 
}: MatchModalProps) {
  const scaleAnim = React.useRef(new Animated.Value(0)).current;
  const opacityAnim = React.useRef(new Animated.Value(0)).current;
  
  React.useEffect(() => {
    if (visible) {
      // Animate in
      Animated.parallel([
        Animated.spring(scaleAnim, {
          toValue: 1,
          useNativeDriver: true,
          tension: 50,
          friction: 8,
        }),
        Animated.timing(opacityAnim, {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
        }),
      ]).start();
    } else {
      // Reset for next time
      scaleAnim.setValue(0);
      opacityAnim.setValue(0);
    }
  }, [visible, scaleAnim, opacityAnim]);
  
  const handleStartChat = () => {
    onStartChat();
    onClose();
  };
  
  return (
    <Modal
      visible={visible}
      transparent
      animationType="none"
      onRequestClose={onClose}
    >
      <Animated.View style={[styles.overlay, { opacity: opacityAnim }]}>
        <TouchableOpacity 
          style={styles.backdrop} 
          activeOpacity={1}
          onPress={onClose}
        />
        
        <Animated.View 
          style={[
            styles.modal,
            {
              transform: [{ scale: scaleAnim }],
            },
          ]}
        >
          {/* Celebration content */}
          <View style={styles.celebration}>
            <Text style={styles.celebrationIcon}>🎉</Text>
            <Text style={styles.matchText}>It's a Match!</Text>
            <Text style={styles.subtitle}>
              You both showed interest in
            </Text>
            <Text style={styles.propertyName} numberOfLines={2}>
              {propertyTitle}
            </Text>
          </View>
          
          {/* Action buttons */}
          <View style={styles.actions}>
            <TouchableOpacity
              style={styles.chatButton}
              onPress={handleStartChat}
            >
              <Text style={styles.chatButtonText}>
                💬 Start Chatting
              </Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={styles.laterButton}
              onPress={onClose}
            >
              <Text style={styles.laterButtonText}>
                Maybe Later
              </Text>
            </TouchableOpacity>
          </View>
          
          {/* Decorative elements */}
          <View style={styles.decorations}>
            <Text style={[styles.decoration, styles.decoration1]}>✨</Text>
            <Text style={[styles.decoration, styles.decoration2]}>🏠</Text>
            <Text style={[styles.decoration, styles.decoration3]}>💫</Text>
            <Text style={[styles.decoration, styles.decoration4]}>🎊</Text>
          </View>
        </Animated.View>
      </Animated.View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  backdrop: {
    ...StyleSheet.absoluteFillObject,
  },
  modal: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.xl,
    margin: theme.spacing.lg,
    padding: theme.spacing.xl,
    maxWidth: screenWidth * 0.9,
    ...theme.shadows.xl,
    overflow: 'hidden',
  },
  celebration: {
    alignItems: 'center',
    marginBottom: theme.spacing.xl,
  },
  celebrationIcon: {
    fontSize: 80,
    marginBottom: theme.spacing.lg,
  },
  matchText: {
    fontSize: theme.typography.fontSize['4xl'],
    fontWeight: theme.typography.fontWeight.bold,
    color: theme.colors.primary,
    marginBottom: theme.spacing.sm,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: theme.typography.fontSize.lg,
    color: theme.colors.textSecondary,
    textAlign: 'center',
    marginBottom: theme.spacing.sm,
  },
  propertyName: {
    fontSize: theme.typography.fontSize.xl,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.text,
    textAlign: 'center',
    lineHeight: theme.typography.lineHeight.tight * theme.typography.fontSize.xl,
  },
  actions: {
    gap: theme.spacing.md,
  },
  chatButton: {
    backgroundColor: theme.colors.primary,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.lg,
    borderRadius: theme.borderRadius.lg,
    alignItems: 'center',
    ...theme.shadows.md,
  },
  chatButtonText: {
    fontSize: theme.typography.fontSize.lg,
    fontWeight: theme.typography.fontWeight.semiBold,
    color: theme.colors.textInverse,
  },
  laterButton: {
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.lg,
    alignItems: 'center',
  },
  laterButtonText: {
    fontSize: theme.typography.fontSize.base,
    fontWeight: theme.typography.fontWeight.medium,
    color: theme.colors.textSecondary,
  },
  decorations: {
    ...StyleSheet.absoluteFillObject,
    pointerEvents: 'none',
  },
  decoration: {
    position: 'absolute',
    fontSize: 24,
    opacity: 0.3,
  },
  decoration1: {
    top: 20,
    left: 20,
  },
  decoration2: {
    top: 30,
    right: 30,
  },
  decoration3: {
    bottom: 60,
    left: 30,
  },
  decoration4: {
    bottom: 40,
    right: 20,
  },
});