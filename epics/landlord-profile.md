# Epic: Landlord Profile Screen

## Problem Statement

Users rate landlords by phone number (hashed for privacy), but there's no way to:
- View aggregated ratings for a specific landlord
- See all rants about a landlord across different properties
- Check a landlord's reputation before signing a lease

## Proposed Solution

### Landlord Profile Screen
- Aggregated scores (fairness, responsiveness, maintenance, privacy)
- Score trends over time (improving/declining)
- Total rant count
- List of properties associated with this landlord
- All rants timeline

### Access Points
1. From Place Profile → tap landlord section
2. From Rant details → tap landlord name
3. Future: Search by phone (requires phone input)

### Privacy Considerations
- Never display actual phone number
- Show only: "Landlord #A7F3" (truncated hash)
- No reverse lookup possible

## Affected Components

### Mobile
- `apps/mobile/src/app/Landlord/LandlordProfile.tsx` (new)
- `apps/mobile/src/components/ScoreTrend.tsx` (new)
- `apps/mobile/src/components/PropertyList.tsx` (new)
- Navigation: add landlord/:tag route

### Backend
- `GET /api/v1/landlords/{tag}` already exists
- May need: score trend calculation, property aggregation

## Technical Spec

### API Response Enhancement
```json
GET /api/v1/landlords/{phoneHash}
{
  "tag": "a7f3b2c1",
  "displayId": "Landlord #A7F3",
  "scores": {
    "fairness": { "avg": 6.2, "count": 12 },
    "responsiveness": { "avg": 5.8, "count": 12 },
    "maintenance": { "avg": 4.5, "count": 12 },
    "privacy": { "avg": 7.1, "count": 12 }
  },
  "trend": "declining",  // based on last 6 months vs prior
  "properties": [
    { "placeId": "uuid", "address": "רחוב הרצל 15, תל אביב", "rantCount": 5 }
  ],
  "rants": [
    { "id": "uuid", "createdAt": "...", "scores": {...}, "comment": "..." }
  ]
}
```

### Score Visualization
- Horizontal bar charts for each score category
- Color coding: red (<5), yellow (5-7), green (>7)
- Trend arrow: ↑ improving, ↓ declining, → stable

## Success Criteria

- [ ] Profile loads from landlord hash/tag
- [ ] All score categories displayed with averages
- [ ] Trend indicator shows improvement/decline
- [ ] Properties list links to Place Profiles
- [ ] Rants timeline scrollable
- [ ] No PII exposed (phone never shown)

## Assignees

- **Swift** (Mobile implementation)
- **Pixel** (Score visualization design)
- **Core** (Backend aggregation if needed)

## Dependencies

- Epic #8 (Rant system must exist)
- Epic #9 (Place Profile for linking)

## Estimated Effort

1.5 sprints

---

*Know your landlord before you sign. Community knowledge protects renters.*
