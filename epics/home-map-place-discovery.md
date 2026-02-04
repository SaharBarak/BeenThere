# Epic: Home Map & Place Discovery

## Problem Statement

BeenThere's primary navigation is map-first. Users should:
1. See a map as their home screen
2. Search any address via Google Places
3. Tap a marker to see community ratings for that place

Currently there's no map implementation.

## Proposed Solution

### Home Screen = Map
- Full-screen map (react-native-maps, Google provider)
- Top search bar (Google Places Autocomplete)
- Bottom FABs: Apartments, Roommates, Rant

### Search → Marker Flow
1. User types address in search bar
2. Autocomplete shows suggestions
3. User selects → map centers on coords
4. **Single circular marker** appears at location
5. Tap marker → navigate to Place Profile

### Place Profile Screen
- Aggregated ratings (landlord + apartment averages)
- Recent rants for this address
- "Add your rant" CTA
- Neighborhood info (if available)

### Backend Integration
```
POST /api/v1/places/snap  # Create/get place by Google Place ID
GET  /api/v1/places/{id}  # Place profile with ratings
```

## Affected Components

- `apps/mobile/src/app/Home/MapScreen.tsx` (new)
- `apps/mobile/src/app/PlaceProfile/PlaceProfile.tsx` (new)
- `apps/mobile/src/components/SearchBar.tsx` (new)
- `apps/mobile/src/components/CircularMarker.tsx` (new)
- `apps/mobile/src/lib/places.ts` (Places API client)

## Technical Spec

### Map Configuration
```typescript
// react-native-maps with Google provider
<MapView
  provider={PROVIDER_GOOGLE}
  initialRegion={{
    latitude: 32.0853,  // Tel Aviv
    longitude: 34.7818,
    latitudeDelta: 0.05,
    longitudeDelta: 0.05,
  }}
  showsUserLocation
/>
```

### Search Behavior
- Debounce input (300ms)
- Show 5 suggestions max
- Support Hebrew + English addresses
- Israel bias for results

### Marker Design
- Circular with gradient border
- Pulse animation on appearance
- Shows rating score inside (if available)

## Success Criteria

- [ ] Map renders with Tel Aviv as default center
- [ ] Search returns Israeli addresses correctly
- [ ] Selecting address centers map + shows marker
- [ ] Tapping marker opens Place Profile
- [ ] Place Profile shows aggregated ratings
- [ ] Performance: <500ms from search to marker

## Assignees

- **Swift** (Mobile implementation)
- **Pixel** (Marker + search UI design)
- **Atlas** (Places API quota monitoring)

## Estimated Effort

2 sprints

---

*The map is the entry point. Make it instant, intuitive, and beautiful.*
