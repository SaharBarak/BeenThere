# BeenThere API Development Roadmap

## Current Status: PR4 - Services Architecture & Infrastructure ✅

**Completed in PR4:**
- ✅ Complete backend architecture restructuring with hexagonal pattern
- ✅ Spring Boot WebFlux + R2DBC configuration
- ✅ Domain entities with proper R2DBC mapping
- ✅ Repository interfaces with reactive CRUD operations
- ✅ Security configuration (permit-all for development)
- ✅ Error handling foundation with ServiceError classes
- ✅ Database migration scripts (Flyway V1)
- ✅ Health and metrics endpoints working
- ✅ Phone number utilities with PII protection (hashing)
- ✅ Jackson configuration for JSON handling

**Architecture Status:**
- Backend compiles and starts successfully
- Health endpoints functional
- Database layer ready (temporarily disabled for development)
- Clean separation of concerns established

---

## Next 3 PRs: Core Business Logic Implementation

### PR5: Core Services & Business Logic 🎯
**Target: Complete core service layer with essential business operations**

**Deliverables:**
- **User Service**: Google OIDC authentication, profile management, user CRUD
- **Place Service**: Place lookup/creation, Google Places integration (mocked), profile aggregation
- **Rant Service**: Combined landlord+apartment rants, roommate rants, transactional operations
- **Basic service structure** for roommates and apartments domains
- **Unit tests** for core service methods
- **Integration tests** with Testcontainers for database operations

**API Endpoints to Implement:**
```
POST /api/v1/auth/google          # User authentication
GET  /api/v1/users/{id}/profile   # User profile
POST /api/v1/places/snap          # Place creation/lookup
GET  /api/v1/places/{id}          # Place profile with ratings
POST /api/v1/rant                 # Combined landlord+apartment rant
POST /api/v1/rant/roommate        # Roommate rating
```

**Success Criteria:**
- All endpoints return proper JSON responses
- Error handling with structured error responses
- Transactional rant creation works end-to-end
- Phone number hashing and PII protection functional
- Database operations tested with real PostgreSQL

---

### PR6: Roommates & Matching System 🤝
**Target: Complete roommate discovery, swiping, and basic messaging**

**Deliverables:**
- **Roommate Discovery Service**: User feed with filtering and pagination
- **Swipe Service**: Like/Pass actions with mutual match detection
- **Match Service**: Match management and basic messaging
- **Feed algorithms**: Seek-based pagination, basic filtering logic
- **Real-time considerations**: Prepare for WebSocket integration later

**API Endpoints to Implement:**
```
GET  /api/v1/roommates/feed       # Paginated roommate discovery
POST /api/v1/swipes               # Roommate swipe actions
GET  /api/v1/matches              # User's matches
GET  /api/v1/matches/{id}/messages # Match messages
POST /api/v1/matches/{id}/messages # Send message
```

**Success Criteria:**
- Roommate feed with proper seek pagination
- Swipe actions create matches correctly
- Message threading works within matches
- Performance tested with sample data
- Rate limiting on swipes implemented

---

### PR7: Apartments & Listings System 🏠
**Target: Complete apartment listings, discovery, and landlord matching**

**Deliverables:**
- **Listing Service**: Apartment creation, management, photo handling
- **Apartment Discovery Service**: Feed with city/price/attribute filtering
- **Listing Match Service**: Seeker likes with auto-accept logic
- **Photo Management**: S3 integration for apartment images
- **Advanced filtering**: Price ranges, attributes, location-based search

**API Endpoints to Implement:**
```
POST /api/v1/apartments           # Create apartment listing
GET  /api/v1/apartments/feed      # Apartment discovery feed
POST /api/v1/listing-swipes       # Like/pass apartment listings
GET  /api/v1/listings/{id}        # Listing details with photos
PUT  /api/v1/listings/{id}        # Update listing (owner only)
```

**Success Criteria:**
- Apartment listings with photo upload
- Advanced filtering and search working
- Auto-accept matching logic implemented
- Owner-only modification permissions
- Integration with map coordinates for location search

---

## Post-MVP Considerations

**PR8+: Polish & Production Readiness**
- Full kotlin-result integration
- Comprehensive error handling
- Rate limiting and security hardening  
- Performance optimization
- Monitoring and observability
- Frontend integration testing

**Future Features:**
- WebSocket real-time messaging
- Push notifications
- Advanced matching algorithms
- Billing/subscription integration
- Mobile app coordination

---

## Technical Debt & Improvements

**Current Technical Debt:**
1. kotlin-result library integration (temporarily disabled)
2. Database connectivity (mocked for development)
3. Google Places API integration (needs real implementation)
4. BlockHound integration for non-blocking verification
5. Comprehensive test coverage

**Infrastructure Dependencies:**
- PostgreSQL database setup
- S3 bucket for photo storage
- Google Places API credentials
- JWT secret management
- Production environment configuration