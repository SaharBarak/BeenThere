# Epic: Search & Filters Enhancement

## Problem Statement

Current search is limited to Google Places autocomplete on the map. Users need:
- Global search across places, landlords, and areas
- Advanced filters for apartments and roommates
- Search history and suggestions
- Area/neighborhood browsing

## Proposed Solution

### Global Search Bar
- Unified search accessible from home screen
- Search types: Places, Areas, Landlords (by ID)
- Recent searches history
- Popular/trending searches

### Enhanced Apartment Filters
- Price range with histogram
- Multiple cities selection
- Commute time filter (to workplace)
- Move-in date filter
- Verified listings toggle

### Enhanced Roommate Filters
- Age range preference
- Occupation/student filter
- Schedule compatibility (night owl, early bird)
- Language preferences
- Budget range

### Area/Neighborhood Browser
- List of popular neighborhoods
- Aggregated stats per area (avg rent, rating)
- "Explore" mode on map

## Affected Components

### Mobile
- `apps/mobile/src/components/GlobalSearch.tsx` (new)
- `apps/mobile/src/components/SearchHistory.tsx` (new)
- `apps/mobile/src/app/Apartments/AdvancedFilters.tsx` (enhance)
- `apps/mobile/src/app/Roommates/RoommateFilters.tsx` (new)
- `apps/mobile/src/app/Explore/AreaBrowser.tsx` (new)

### Backend
- `GET /api/v1/search?q=` (unified search endpoint)
- `GET /api/v1/areas` (neighborhoods list with stats)
- Enhanced filter params on existing feeds

## Technical Spec

### Unified Search
```typescript
GET /api/v1/search?q=herzl&types=place,area,landlord
{
  "results": [
    { "type": "place", "id": "uuid", "title": "רחוב הרצל 15", "subtitle": "תל אביב" },
    { "type": "area", "id": "herzliya", "title": "הרצליה", "subtitle": "120 דירות" },
    { "type": "landlord", "id": "a7f3", "title": "Landlord #A7F3", "subtitle": "12 דירוגים" }
  ]
}
```

### Search History (Local)
```typescript
// AsyncStorage
const searchHistory = [
  { query: "רחוב דיזנגוף", timestamp: 1699..., type: "place" },
  { query: "תל אביב", timestamp: 1699..., type: "area" }
]
```

## Success Criteria

- [ ] Global search returns mixed results
- [ ] Search history persists locally
- [ ] Apartment filters include price histogram
- [ ] Roommate filters include lifestyle preferences
- [ ] Area browser shows neighborhood stats
- [ ] Search feels instant (<300ms)

## Assignees

- **Swift** (Mobile search UI)
- **Core** (Backend search endpoint)
- **Pixel** (Filter UI design)

## Estimated Effort

2 sprints

---

*Great search makes everything discoverable. Don't make users hunt.*
