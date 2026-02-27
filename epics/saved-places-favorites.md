# Epic: Saved Places & Favorites

## Problem Statement

Users discover places and apartments but have no way to:
- Save places for later comparison
- Track apartments they're interested in
- Get notified when new rants appear on saved places
- Organize saved items into lists

## Proposed Solution

### Save Button
- Heart/bookmark icon on Place Profile and Apartment cards
- Visual feedback on save/unsave
- Save count visible (social proof)

### Saved Places Tab
- List of all saved places
- Filter: Places vs Apartments
- Sort: Recently saved, Most rants, Highest rated
- Remove from saved (swipe or tap)

### Collections (Phase 2)
- Create named collections ("Tel Aviv options", "Budget picks")
- Move saved items between collections
- Share collection link

### Notifications Integration
- "New rant on saved place" notification
- "Price drop on saved apartment" (if applicable)

## Affected Components

### Mobile
- `apps/mobile/src/components/SaveButton.tsx` (new)
- `apps/mobile/src/app/Saved/SavedList.tsx` (new)
- `apps/mobile/src/app/Saved/SavedFilters.tsx` (new)
- Navigation: add Saved tab to bottom nav

### Backend
- `saved_places` table (user_id, place_id, saved_at)
- `saved_listings` table (user_id, listing_id, saved_at)
- `POST /api/v1/saved/places/{id}` (save)
- `DELETE /api/v1/saved/places/{id}` (unsave)
- `GET /api/v1/saved/places` (list)
- Same endpoints for listings

## Technical Spec

### Optimistic UI
```typescript
const saveMutation = useMutation({
  mutationFn: (placeId) => api.post(`/saved/places/${placeId}`),
  onMutate: async (placeId) => {
    // Optimistically update UI
    queryClient.setQueryData(['place', placeId], old => ({
      ...old,
      isSaved: true
    }))
  }
})
```

### Backend Tables
```sql
CREATE TABLE saved_places (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  place_id UUID NOT NULL REFERENCES places(id),
  saved_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, place_id)
);

CREATE TABLE saved_listings (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  listing_id UUID NOT NULL REFERENCES listings(id),
  saved_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, listing_id)
);
```

## Success Criteria

- [ ] Save button on all place/apartment cards
- [ ] Saved tab shows all saved items
- [ ] Filters and sort work correctly
- [ ] Optimistic UI feels instant
- [ ] Notification on new rant for saved place
- [ ] Sync across devices (backend-stored)

## Assignees

- **Swift** (Mobile implementation)
- **Core** (Backend endpoints)
- **Pixel** (Save button + list design)

## Estimated Effort

1.5 sprints

---

*Let users curate their own shortlist. Comparison drives decisions.*
