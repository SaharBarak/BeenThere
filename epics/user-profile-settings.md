# Epic: User Profile & Settings

## Problem Statement

Users need to:
- View and edit their profile
- Manage matching preferences
- Control notification settings
- View their own rants history
- Sign out

Currently no profile/settings screens exist.

## Proposed Solution

### Profile Screen (Tab)
- Display current profile (photo, name, bio)
- Stats: rants count, matches count
- Edit profile button
- My Rants section (list of user's rants)

### Edit Profile Screen
- Update photo, name, bio
- Update status (has apartment, looking for, matching)
- Save to PUT /api/v1/users/me/profile

### Settings Screen
- **Matching Preferences**
  - Budget range
  - Location preferences
  - Roommate preferences (pets, smoking, schedule)
- **Notifications** (future)
  - Match alerts
  - Message alerts
  - New rants for saved places
- **Account**
  - Sign out
  - Delete account (Phase 2)
- **About**
  - Version, Terms, Privacy

### My Rants Section
- List of user's submitted rants
- Tap to view details
- Option to delete own rant

## Affected Components

- `apps/mobile/src/app/Profile/ProfileScreen.tsx` (new)
- `apps/mobile/src/app/Profile/EditProfile.tsx` (new)
- `apps/mobile/src/app/Profile/Settings.tsx` (new)
- `apps/mobile/src/app/Profile/MyRants.tsx` (new)
- `apps/mobile/src/components/RantCard.tsx` (new)
- `apps/mobile/src/lib/user.ts` (API client)

## Technical Spec

### User State (Zustand)
```typescript
interface UserStore {
  user: User | null
  setUser: (user: User) => void
  updateProfile: (updates: Partial<User>) => void
  logout: () => void
}
```

### API Endpoints Used
```
GET  /api/v1/users/me/profile
PUT  /api/v1/users/me/profile
GET  /api/v1/users/me/rants
DELETE /api/v1/rants/{id}
```

## Success Criteria

- [ ] Profile displays current user info
- [ ] Edit profile saves to backend
- [ ] Settings persist across sessions
- [ ] My Rants shows user's submissions
- [ ] Sign out clears auth state
- [ ] All screens RTL compatible

## Assignees

- **Swift** (Mobile implementation)
- **Pixel** (Profile UI design)

## Dependencies

- Epic #6 (Mobile App Foundation)

## Estimated Effort

1.5 sprints

---

*Your profile is your identity in the community. Make it personal.*
