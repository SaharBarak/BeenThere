# Epic: Roommate Matching UI (Tinder-style)

## Problem Statement

The backend has complete roommate matching infrastructure:
- `GET /api/v1/roommates/feed` - paginated discovery
- `POST /api/v1/swipes` - like/pass actions
- `GET /api/v1/matches` - mutual matches
- `GET/POST /api/v1/matches/{id}/messages` - chat

But there's no mobile UI for users to discover and match with roommates.

## Proposed Solution

### Swipe Deck Screen
- Card stack showing potential roommates
- Photo, name, bio, preferences visible
- **Swipe right = LIKE**, **Swipe left = PASS**
- Visual feedback (green/red overlay on swipe)
- "It's a match!" celebration modal on mutual like

### Matches List Screen
- Grid/list of matched users
- Unread message indicator
- Tap → opens chat

### Chat Screen
- Simple text messaging (MVP)
- Message bubbles (sent/received)
- Timestamp grouping
- Send button + text input

### Filters (Optional)
- Has apartment / Looking for apartment
- Pets OK / Smoking OK
- Budget range

## Affected Components

- `apps/mobile/src/app/Roommates/SwipeDeck.tsx` (new)
- `apps/mobile/src/app/Roommates/MatchesList.tsx` (new)
- `apps/mobile/src/app/Roommates/Chat.tsx` (new)
- `apps/mobile/src/components/SwipeCard.tsx` (new)
- `apps/mobile/src/components/MatchModal.tsx` (new)
- `apps/mobile/src/lib/roommates.ts` (API client)

## Technical Spec

### Swipe Gestures
```typescript
// react-native-gesture-handler + react-native-reanimated
// Threshold: 120px horizontal for action
// Spring animation on release
```

### API Integration
```typescript
// Fetch feed with cursor pagination
const { data, fetchNextPage } = useInfiniteQuery({
  queryKey: ['roommates', 'feed'],
  queryFn: ({ pageParam }) => api.get(`/roommates/feed?cursor=${pageParam}`)
})

// Submit swipe
const swipeMutation = useMutation({
  mutationFn: (data) => api.post('/swipes', data),
  onSuccess: (result) => {
    if (result.matchId) showMatchModal(result.matchId)
  }
})
```

### Chat Polling (MVP)
- Poll messages every 5 seconds when chat is open
- TanStack Query with refetchInterval
- Future: WebSocket upgrade

## Success Criteria

- [ ] Swipe deck loads from `/roommates/feed`
- [ ] Swipe right sends LIKE, left sends PASS
- [ ] Mutual match triggers celebration modal
- [ ] Matches list shows all matches with unread indicator
- [ ] Chat sends/receives messages
- [ ] Smooth 60fps swipe animations

## Assignees

- **Swift** (Mobile implementation)
- **Flow** (Swipe UX/animations)
- **Core** (Backend support if needed)

## Dependencies

- Epic #6 (Mobile App Foundation) must be complete

## Estimated Effort

2 sprints

---

*Finding a roommate should feel fun, not like filling out forms.*
