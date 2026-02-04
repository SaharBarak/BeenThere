# Epic: Mobile App Foundation

## Problem Statement

BeenThere's backend API is progressing well (auth, rants, roommates, apartments), but the mobile app (`apps/mobile`) contains only a screen definition file. We need a fully scaffolded React Native (Expo) app to connect users to the API.

## Proposed Solution

Create the Expo React Native foundation with:

1. **Expo SDK scaffold** (TypeScript strict mode)
2. **RTL + Hebrew (he-IL)** i18n configuration
3. **Google OAuth sign-in** flow
4. **React Navigation** (stack + tabs)
5. **TanStack Query** for API layer
6. **Zustand** for local state
7. **Core screens**: SignIn, Profile, Home tabs

## Affected Components

- `apps/mobile/` (new Expo app)
- `packages/contracts/` (shared Zod schemas for FE validation)

## Technical Spec

### Dependencies
```json
{
  "expo": "~52.x",
  "expo-auth-session": "*",
  "react-navigation": "^6.x",
  "@tanstack/react-query": "^5.x",
  "zustand": "^4.x",
  "i18next": "^23.x",
  "react-i18next": "*",
  "zod": "^3.x"
}
```

### Directory Structure
```
apps/mobile/
├── app.json
├── App.tsx
├── src/
│   ├── app/
│   │   ├── navigation/
│   │   ├── screens/
│   │   │   ├── SignIn.tsx
│   │   │   ├── Onboarding/
│   │   │   └── Home/
│   ├── components/
│   ├── lib/
│   │   ├── api.ts
│   │   ├── auth.ts
│   │   └── queryClient.ts
│   ├── state/
│   ├── i18n/
│   │   └── he.json
│   └── validators/
```

### RTL Configuration
- `I18nManager.forceRTL(true)` on app launch
- Fonts: Rubik/Heebo
- All layouts respect `flexDirection` flip

## Success Criteria

- [ ] `npx expo start` launches without errors
- [ ] Google sign-in obtains ID token
- [ ] App sends token to `POST /api/v1/auth/google` and stores JWT
- [ ] Navigation between Sign In → Profile → Home works
- [ ] Hebrew strings display correctly in RTL
- [ ] TanStack Query fetches from API with auth headers

## Assignees

- **Swift** (Mobile lead)
- **Flow** (UI/UX support)

## Estimated Effort

2-3 sprints (1 week per sprint)

---

*This epic establishes the mobile foundation that all other mobile features will build upon.*
