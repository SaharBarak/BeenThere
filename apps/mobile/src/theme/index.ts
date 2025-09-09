/**
 * BeenThere design system - Housing-focused theme
 * Trust-building colors and modern typography for roommate/apartment matching
 */

export const theme = {
  colors: {
    // Primary - Trust & Safety
    primary: '#3B82F6', // Blue - trustworthy, professional
    primaryDark: '#1D4ED8',
    primaryLight: '#93C5FD',
    
    // Secondary - Housing & Warmth
    secondary: '#10B981', // Green - positive, growth, "home"
    secondaryDark: '#047857',
    secondaryLight: '#86EFAC',
    
    // Accent - Energy & Matches
    accent: '#F59E0B', // Amber - energy, matches, premium
    accentDark: '#D97706',
    accentLight: '#FCD34D',
    
    // Neutrals
    background: '#FFFFFF',
    backgroundSecondary: '#F8FAFC',
    backgroundTertiary: '#F1F5F9',
    
    surface: '#FFFFFF',
    surfaceSecondary: '#F8FAFC',
    
    // Text
    text: '#1F2937',
    textSecondary: '#6B7280',
    textTertiary: '#9CA3AF',
    textInverse: '#FFFFFF',
    
    // Status & Actions
    success: '#10B981',
    warning: '#F59E0B',
    error: '#EF4444',
    info: '#3B82F6',
    
    // Swipe Actions
    like: '#10B981', // Green for interested
    pass: '#EF4444', // Red for pass
    superLike: '#8B5CF6', // Purple for super interested
    
    // Borders & Dividers
    border: '#E5E7EB',
    borderLight: '#F3F4F6',
    
    // Overlays & Shadows
    overlay: 'rgba(0, 0, 0, 0.5)',
    shadow: 'rgba(0, 0, 0, 0.1)',
  },
  
  typography: {
    // Font families
    fontFamily: {
      regular: 'System',
      medium: 'System',
      semiBold: 'System',
      bold: 'System',
    },
    
    // Font sizes (using 8pt grid system)
    fontSize: {
      xs: 12,
      sm: 14,
      base: 16,
      lg: 18,
      xl: 20,
      '2xl': 24,
      '3xl': 32,
      '4xl': 40,
      '5xl': 48,
    },
    
    // Line heights
    lineHeight: {
      tight: 1.2,
      normal: 1.4,
      relaxed: 1.6,
    },
    
    // Font weights
    fontWeight: {
      normal: '400',
      medium: '500',
      semiBold: '600',
      bold: '700',
    },
  },
  
  spacing: {
    // 8pt grid system
    xs: 4,
    sm: 8,
    md: 16,
    lg: 24,
    xl: 32,
    '2xl': 40,
    '3xl': 48,
    '4xl': 64,
    '5xl': 80,
  },
  
  borderRadius: {
    none: 0,
    sm: 4,
    md: 8,
    lg: 12,
    xl: 16,
    '2xl': 24,
    full: 9999,
  },
  
  shadows: {
    sm: {
      shadowColor: '#000000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.05,
      shadowRadius: 2,
      elevation: 1,
    },
    md: {
      shadowColor: '#000000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.1,
      shadowRadius: 4,
      elevation: 2,
    },
    lg: {
      shadowColor: '#000000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.15,
      shadowRadius: 8,
      elevation: 4,
    },
    xl: {
      shadowColor: '#000000',
      shadowOffset: { width: 0, height: 8 },
      shadowOpacity: 0.2,
      shadowRadius: 16,
      elevation: 8,
    },
  },
} as const;

export type Theme = typeof theme;