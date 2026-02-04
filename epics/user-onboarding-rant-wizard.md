# Epic: User Onboarding & Initial Rant Wizard

## Problem Statement

BeenThere's value prop is community-driven ratings. The onboarding flow must:
1. Get users signed up quickly (Google only)
2. Collect minimal profile info
3. **Require an initial rant** to seed content

Without forcing the first rant, users consume but don't contribute.

## Proposed Solution

### Onboarding Flow (4 steps)

1. **Google Sign-In** → Get ID token → Exchange for JWT
2. **Profile Setup** → Display name, photo, short bio (≤140 chars)
3. **Status Questions** (3 quick taps):
   - יש לך דירה? (כן/לא)
   - מה מחפשים? (דירה / שותפים / רק לפרוק)
   - פתוח/ה למאצ'ינג? (כן/לא)
4. **Initial Rant Wizard** (required):
   - Landlord scores (fairness, response, maintenance, privacy)
   - Apartment scores (condition, noise, utilities, sunlight/mold)
   - Optional extras (neighbors, roof, elevator, neighborhood)
   - Landlord phone (hashed server-side)
   - Apartment via Google Places autocomplete
   - "I still live here" checkbox

### Backend Endpoints Used
```
POST /api/v1/auth/google      # Step 1
PUT  /api/v1/users/me/profile # Step 2
POST /api/v1/rant             # Step 4 (combined landlord+apartment)
```

## Affected Components

- `apps/mobile/src/app/Onboarding/` (new screens)
- `apps/mobile/src/components/RatingSliders.tsx`
- `apps/mobile/src/components/PhoneInput.tsx`
- `apps/mobile/src/components/PlaceAutocomplete.tsx`
- Backend: Endpoints already exist

## UI/UX Spec

### Screen Flow
```
SignIn → Profile → StatusQuestions → RantWizard (multi-step) → Home
```

### Rant Wizard Steps
1. **Intro**: "בוא נתחיל - ספר/י על החוויה האחרונה שלך"
2. **Landlord Rating**: 4 sliders (1-10 scale)
3. **Apartment Rating**: 4 sliders + optional extras
4. **Identify**: Phone input + Place autocomplete
5. **Confirm**: Preview + submit

### Validation
- All landlord/apartment scores required (1-10)
- Phone must be valid Israeli format (+972)
- Place must be selected from autocomplete

## Success Criteria

- [ ] User can complete full onboarding in <3 minutes
- [ ] First rant is created in database
- [ ] User lands on Home after wizard
- [ ] Analytics: track completion rate per step
- [ ] Phone number never sent to client (only submitted, hashed server-side)

## Assignees

- **Swift** (Mobile implementation)
- **Pixel** (UI design)
- **Core** (Backend validation)

## Estimated Effort

2 sprints

---

*The initial rant is the activation moment. Make it frictionless but required.*
