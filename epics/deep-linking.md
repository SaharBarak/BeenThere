# Epic: Deep Linking & Sharing

## Problem Statement

Users can't share:
- Place profiles with friends
- Apartment listings
- Landlord profiles
- Their own profile for roommate matching

Sharing drives organic growth and helps users collaborate on apartment hunting.

## Proposed Solution

### Universal Links
- `beenthere.app/place/{id}` → Place Profile
- `beenthere.app/apartment/{id}` → Apartment Details
- `beenthere.app/landlord/{tag}` → Landlord Profile
- `beenthere.app/user/{id}` → User Profile (for matching)

### Share Button
- Native share sheet on all shareable screens
- Preview card with image, title, description
- Copy link option

### Deep Link Handling
- App intercepts universal links
- Opens correct screen with data
- Fallback to web (or app store) if not installed

### Social Preview (OG Tags)
- Rich previews in WhatsApp, Telegram, etc.
- Dynamic OG image generation
- Hebrew + English metadata

## Affected Components

### Mobile
- `apps/mobile/src/lib/deepLinks.ts` (link handling)
- `apps/mobile/src/components/ShareButton.tsx` (new)
- Navigation: handle incoming links
- app.json: configure URL schemes

### Backend
- `GET /api/v1/og/place/{id}` (OG metadata)
- `GET /api/v1/og/apartment/{id}`
- `GET /api/v1/og/landlord/{tag}`
- Dynamic image generation service (optional)

### Web (Optional)
- Landing pages for link previews
- App store redirect for non-installed users

## Technical Spec

### Expo Deep Links
```typescript
// app.json
{
  "expo": {
    "scheme": "beenthere",
    "web": {
      "bundler": "metro"
    },
    "android": {
      "intentFilters": [{
        "action": "VIEW",
        "data": [{ "scheme": "https", "host": "beenthere.app" }]
      }]
    },
    "ios": {
      "associatedDomains": ["applinks:beenthere.app"]
    }
  }
}
```

### Link Parsing
```typescript
const parseDeepLink = (url: string) => {
  const patterns = [
    { regex: /\/place\/([^\/]+)/, screen: 'PlaceProfile', param: 'placeId' },
    { regex: /\/apartment\/([^\/]+)/, screen: 'ApartmentDetails', param: 'listingId' },
    { regex: /\/landlord\/([^\/]+)/, screen: 'LandlordProfile', param: 'tag' },
  ]
  // Match and return navigation action
}
```

## Success Criteria

- [ ] Share button on Place, Apartment, Landlord, Profile screens
- [ ] Native share sheet opens with preview
- [ ] Links open correct screen in app
- [ ] Rich previews in messaging apps
- [ ] Fallback works for non-installed users

## Assignees

- **Swift** (Mobile deep link handling)
- **Core** (Backend OG endpoints)
- **Atlas** (Domain + hosting setup)

## Estimated Effort

1.5 sprints

---

*Every share is a potential new user. Make sharing effortless.*
