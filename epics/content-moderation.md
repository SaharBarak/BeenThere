# Epic: Content Moderation & Reporting

## Problem Statement

User-generated content (rants, messages, profiles) may contain:
- Harassment or hate speech
- False/defamatory claims
- Personal information exposure
- Spam or promotional content

We need tools for users to report issues and for admins to moderate.

## Proposed Solution

### User Reporting (Mobile)
- Report button on: rants, profiles, messages
- Report reasons: Harassment, False info, PII exposure, Spam, Other
- Optional comment field
- Confirmation: "Thanks for reporting"

### Backend Moderation Queue
- Reports table with status (pending/reviewed/actioned)
- Admin endpoints to list/review/action reports
- Actions: dismiss, hide content, warn user, ban user

### Automated Filters (Phase 1)
- Profanity filter on rant comments
- Phone/email detection in text (flag for review)
- Rate limiting on rant submissions

### Admin Dashboard (Future)
- Web interface for moderation queue
- User management (warnings, bans)
- Content analytics

## Affected Components

### Mobile
- `apps/mobile/src/components/ReportButton.tsx` (new)
- `apps/mobile/src/components/ReportModal.tsx` (new)
- Integration points: RantCard, ProfileScreen, Chat

### Backend
- `reports` table (reporter_id, content_type, content_id, reason, status)
- `POST /api/v1/reports` (create report)
- `GET /api/v1/admin/reports` (list pending)
- `PUT /api/v1/admin/reports/{id}` (review/action)
- Profanity filter service
- Rate limiter middleware

## Technical Spec

### Reports Table
```sql
CREATE TABLE reports (
  id UUID PRIMARY KEY,
  reporter_user_id UUID NOT NULL REFERENCES users(id),
  content_type TEXT NOT NULL CHECK (content_type IN ('rant', 'profile', 'message')),
  content_id UUID NOT NULL,
  reason TEXT NOT NULL CHECK (reason IN ('harassment', 'false_info', 'pii', 'spam', 'other')),
  comment TEXT,
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'reviewed', 'actioned', 'dismissed')),
  admin_notes TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  reviewed_at TIMESTAMPTZ
);
```

### Report Flow
```
User taps report → Modal with reasons → Submit → POST /api/v1/reports
→ Backend queues for review → Admin reviews → Action taken
→ If content hidden, notify content owner
```

## Success Criteria

- [ ] Report button visible on rants, profiles, messages
- [ ] Report modal captures reason + optional comment
- [ ] Reports stored in database
- [ ] Profanity filter blocks obvious violations
- [ ] Rate limiting prevents spam (max 3 rants/day)
- [ ] Admin can list and action reports via API

## Assignees

- **Swift** (Mobile report UI)
- **Core** (Backend reports + filters)
- **Keeper** (Test moderation flows)

## Dependencies

- Epic #8 (Rant system)
- Epic #10 (Chat for message reporting)

## Estimated Effort

2 sprints

---

*A safe community is a thriving community. Empower users to protect each other.*
