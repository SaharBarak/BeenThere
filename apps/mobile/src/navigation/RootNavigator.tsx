/**
 * Root Navigation for BeenThere
 * Tab-based navigation with housing-focused screens
 */

import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';

// Import screens
import DiscoverScreen from '../app/DiscoverScreen';
import ChatListScreen from '../app/ChatListScreen';
import RatingsScreen from '../app/RatingsScreen';
import ProfileScreen from '../app/ProfileScreen';
import PaywallScreen from '../app/PaywallScreen';

import { theme } from '../theme';

export type RootStackParamList = {
  MainTabs: undefined;
  Paywall: undefined;
};

export type MainTabParamList = {
  Discover: undefined;
  Chats: undefined;
  Ratings: undefined;
  Profile: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<MainTabParamList>();

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap;

          switch (route.name) {
            case 'Discover':
              iconName = focused ? 'home' : 'home-outline';
              break;
            case 'Chats':
              iconName = focused ? 'chatbubbles' : 'chatbubbles-outline';
              break;
            case 'Ratings':
              iconName = focused ? 'star' : 'star-outline';
              break;
            case 'Profile':
              iconName = focused ? 'person' : 'person-outline';
              break;
            default:
              iconName = 'home-outline';
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: theme.colors.primary,
        tabBarInactiveTintColor: theme.colors.textTertiary,
        tabBarStyle: {
          backgroundColor: theme.colors.surface,
          borderTopColor: theme.colors.border,
          paddingTop: 4,
          paddingBottom: 4,
        },
        headerShown: false,
      })}
    >
      <Tab.Screen 
        name="Discover" 
        component={DiscoverScreen}
        options={{ tabBarLabel: 'Discover' }}
      />
      <Tab.Screen 
        name="Chats" 
        component={ChatListScreen}
        options={{ tabBarLabel: 'Chats' }}
      />
      <Tab.Screen 
        name="Ratings" 
        component={RatingsScreen}
        options={{ tabBarLabel: 'Ratings' }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ tabBarLabel: 'Profile' }}
      />
    </Tab.Navigator>
  );
}

export default function RootNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="MainTabs" component={MainTabs} />
        <Stack.Screen 
          name="Paywall" 
          component={PaywallScreen}
          options={{
            presentation: 'modal',
            headerShown: true,
            title: 'BeenThere Premium',
            headerStyle: {
              backgroundColor: theme.colors.primary,
            },
            headerTintColor: theme.colors.textInverse,
          }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}