/**
 * Data Transfer Objects (DTOs) for BeenThere API
 * Mirrors server DTOs exactly - DO NOT modify without backend coordination
 */

// Core Entity Types
export type ListingType = "ROOMMATE_GROUP" | "ENTIRE_PLACE";
export type SwipeTarget = "USER" | "LISTING";
export type SwipeAction = "LIKE" | "PASS";
export type SubStatus = "NONE" | "ACTIVE" | "EXPIRED" | "CANCELED";

// Listing DTOs
export interface ListingCardDTO {
  id: string;
  type: ListingType;
  title: string;
  city: string;
  price: number;
  createdAt: string; // ISO date string
}

// Feed Response (Generic for cursor pagination)
export interface FeedResponse<T> {
  items: T[];
  nextCursor?: string | null;
}

// Swipe DTOs
export interface SwipeRequest {
  targetType: SwipeTarget;
  targetId: string;
  action: SwipeAction;
}

export interface SwipeResponse {
  matchId?: string | null;
}

// Billing DTOs
export interface BillingStatus {
  status: SubStatus;
  periodEnd?: string; // ISO date string
}

// User DTOs
export interface UserProfileDTO {
  id: string;
  email: string;
  name: string;
  bio?: string;
  profileImageUrl?: string;
  createdAt: string;
}

// Match DTOs
export interface MatchDTO {
  id: string;
  listingId: string;
  userId: string;
  landlordId: string;
  createdAt: string;
  status: "ACTIVE" | "EXPIRED" | "ARCHIVED";
}

// Message DTOs
export interface MessageDTO {
  id: string;
  matchId: string;
  senderId: string;
  content: string;
  createdAt: string;
  readAt?: string;
}

// Rating DTOs
export interface RatingRequest {
  targetType: "HOUSE" | "ROOMMATE" | "LANDLORD";
  targetId: string;
  scores: Record<string, number>; // e.g. { "cleanliness": 5, "communication": 4 }
  comment?: string;
}

export interface RatingDTO {
  id: string;
  targetType: "HOUSE" | "ROOMMATE" | "LANDLORD";
  targetId: string;
  raterId: string;
  scores: Record<string, number>;
  comment?: string;
  createdAt: string;
}