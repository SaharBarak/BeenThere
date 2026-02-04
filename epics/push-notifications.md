# Epic: Push Notifications

## Problem Statement

Users need to be notified about:
- New roommate matches
- New messages from matches
- New rants on places they've rated
- Weekly engagement nudges

Without push notifications, users must manually check the app for updates.

## Proposed Solution

### Notification Types

1. **Match Alerts**
   - "You have a new match with {name}!"
   - Tap → opens match chat

2. **Message Alerts**
   - "{name} sent you a message"
   - Tap → opens chat thread

3. **Place Activity**
   - "New rant posted for {address}"
   - For places user has rated
   - Tap → opens place profile

4. **Engagement Nudges** (weekly)
   - "See what's new in your area"
   - "X new apartments match your filters"

### Technical Implementation

- **Expo Push Notifications** (expo-notifications)
- **Backend**: Store push tokens, send via Expo Push API
- **Preferences**: Per-type toggles in Settings

## Affected Components

### Mobile
- `apps/mobile/src/lib/notifications.ts` (registration, handlers)
- `apps/mobile/src/app/Profile/Settings.tsx` (notification toggles)
- App.tsx (notification listeners)

### Backend
- `push_tokens` table (user_id, expo_push_token, platform)
- `POST /api/v1/users/me/push-token` (register token)
- `DELETE /api/v1/users/me/push-token` (unregister)
- Notification service (sends via Expo Push API)
- Event triggers (match created, message sent, rant posted)

## Technical Spec

### Mobile Registration
```typescript
import * as Notifications from 'expo-notifications';

async function registerForPush() {
  const { status } = await Notifications.requestPermissionsAsync();
  if (status !== 'granted') return;
  
  const token = await Notifications.getExpoPushTokenAsync();
  await api.post('/users/me/push-token', { token: token.data });
}
```

### Backend Notification Service
```kotlin
@Service
class NotificationService(
  private val pushTokenRepository: PushTokenRepository,
  private val expoPushClient: ExpoPushClient
) {
  suspend fun notifyMatch(userId: UUID, matchName: String) {
    val tokens = pushTokenRepository.findByUserId(userId)
    expoPushClient.send(tokens, PushMessage(
      title = "התאמה חדשה! 🎉",
      body = "יש לך התאמה עם $matchName",
      data = mapOf("type" to "match", "matchId" to matchId)
    ))
  }
}
```

## Success Criteria

- [ ] Push token registered on app launch (with permission)
- [ ] Match notifications delivered within 30 seconds
- [ ] Message notifications delivered within 10 seconds
- [ ] Tapping notification opens correct screen
- [ ] Notification preferences respected
- [ ] Uninstall/logout clears push token

## Assignees

- **Swift** (Mobile implementation)
- **Core** (Backend notification service)
- **Atlas** (Expo Push API setup)

## Dependencies

- Epic #6 (Mobile App Foundation)
- Epic #10 (Roommate Matching - for match/message triggers)

## Estimated Effort

2 sprints

---

*Don't make users check for updates. Bring updates to them.*
