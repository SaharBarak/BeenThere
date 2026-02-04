# Epic: Apartments List & Discovery

## Problem Statement

Users need to browse available apartments beyond the map view. The backend has:
- `GET /api/v1/apartments/feed` - paginated listings
- `POST /api/v1/listing-swipes` - like/pass apartments
- Listing creation and photo upload endpoints

No mobile UI exists for apartment discovery.

## Proposed Solution

### Apartments List Screen
- Vertical scrolling list of apartment cards
- Each card: photos (carousel), price, location, key attributes
- Tap card → Apartment Details

### Filters Modal
- Price range (₪ slider)
- City/Area selection
- Rooms count
- Attributes: pets OK, smoking OK, furnished, elevator, parking

### Apartment Details Screen
- Photo gallery (full-screen swipe)
- Full description
- Amenities list
- Location on mini-map
- Contact/Like button
- Related rants for this address

### Like/Save Flow
- Heart icon to save apartments
- Saved apartments tab
- Optional: notify listing owner of interest

## Affected Components

- `apps/mobile/src/app/Apartments/List.tsx` (new)
- `apps/mobile/src/app/Apartments/Details.tsx` (new)
- `apps/mobile/src/app/Apartments/Filters.tsx` (new)
- `apps/mobile/src/components/ApartmentCard.tsx` (new)
- `apps/mobile/src/components/PhotoCarousel.tsx` (new)
- `apps/mobile/src/lib/apartments.ts` (API client)

## Technical Spec

### API Integration
```typescript
// Paginated feed with filters
const { data, fetchNextPage } = useInfiniteQuery({
  queryKey: ['apartments', 'feed', filters],
  queryFn: ({ pageParam }) => 
    api.get(`/apartments/feed?cursor=${pageParam}&${filterParams}`)
})
```

### Filter State
```typescript
interface ApartmentFilters {
  priceMin?: number
  priceMax?: number
  city?: string
  rooms?: number[]
  pets?: boolean
  smoking?: boolean
  furnished?: boolean
}
```

## Success Criteria

- [ ] List loads apartments from feed endpoint
- [ ] Filters update results in real-time
- [ ] Details screen shows all apartment info
- [ ] Photo carousel works smoothly
- [ ] Like/save persists across sessions
- [ ] Performance: smooth scroll at 60fps

## Assignees

- **Swift** (Mobile implementation)
- **Pixel** (Card/details design)
- **Core** (Backend support if needed)

## Dependencies

- Epic #6 (Mobile App Foundation)

## Estimated Effort

2 sprints

---

*Finding an apartment should feel like browsing, not hunting.*
