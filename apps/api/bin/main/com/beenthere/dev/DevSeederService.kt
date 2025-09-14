package com.beenthere.dev

import com.beenthere.entities.*
import com.beenthere.repositories.*
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

/**
 * Development data seeder - populates database with test data
 * Only runs in 'dev' profile and only if database is empty
 */
@Service
@Profile("dev")
class DevSeederService(
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val landlordRepository: LandlordRepository,
    private val listingRepository: ListingRepository,
    private val listingPhotoRepository: ListingPhotoRepository,
    private val listingMemberRepository: ListingMemberRepository,
    private val rantGroupRepository: RantGroupRepository,
    private val ratingLandlordRepository: RatingLandlordRepository,
    private val ratingApartmentRepository: RatingApartmentRepository,
    private val ratingRoommateRepository: RatingRoommateRepository
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        runBlocking {
            seedDatabaseIfEmpty()
        }
    }
    
    private suspend fun seedDatabaseIfEmpty() {
        println("🌱 DevSeederService: Checking if database seeding is needed...")
        
        val userCount = userRepository.count()
        if (userCount == 0L) {
            println("🌱 DevSeederService: Database is empty, seeding with test data...")
            seedTestData()
        } else {
            println("🌱 DevSeederService: Database already contains data ($userCount users), skipping seeding.")
        }
    }
    
    private suspend fun seedTestData() {
        println("🌱 Creating test users...")
        
        // Create test users
        val user1 = UserEntity(
            googleSub = "google_sub_owner_1",
            email = "owner@test.com",
            displayName = "John Owner",
            photoUrl = "https://example.com/photos/john.jpg",
            bio = "Looking to rent out my awesome apartment!"
        )
        
        val user2 = UserEntity(
            googleSub = "google_sub_seeker_1",
            email = "seeker@test.com", 
            displayName = "Jane Seeker",
            photoUrl = "https://example.com/photos/jane.jpg",
            bio = "Computer science student looking for a great place to live"
        )
        
        val user3 = UserEntity(
            googleSub = "google_sub_roommate_1",
            email = "roommate@test.com",
            displayName = "Mike Roommate", 
            photoUrl = "https://example.com/photos/mike.jpg",
            bio = "Software engineer, clean and quiet"
        )
        
        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)
        val savedUser3 = userRepository.save(user3)
        
        println("🌱 Creating test places...")
        
        // Create test places
        val place1 = PlaceEntity(
            googlePlaceId = "ChIJN1t_tDeuEmsRUsoyG83frY4",
            formattedAddress = "123 Test Street, Tel Aviv, Israel",
            lat = 32.0853,
            lng = 34.7818
        )
        
        val place2 = PlaceEntity(
            googlePlaceId = "ChIJVXealLU_xkcRja_At0z9AGY",
            formattedAddress = "456 Sample Avenue, Haifa, Israel", 
            lat = 32.7767,
            lng = 34.9896
        )
        
        val savedPlace1 = placeRepository.save(place1)
        val savedPlace2 = placeRepository.save(place2)
        
        println("🌱 Creating test landlords...")
        
        // Create test landlords
        val landlord1 = LandlordEntity(
            phoneHash = "hashed_phone_123_example"
        )
        
        val savedLandlord1 = landlordRepository.save(landlord1)
        
        println("🌱 Creating test listings...")
        
        // Create test listings
        val listing1 = ListingEntity(
            ownerUserId = savedUser1.id!!,
            placeId = savedPlace1.id!!,
            title = "Beautiful 2BR Apartment in Tel Aviv",
            price = 6500,
            autoAccept = false,
            type = "ENTIRE_PLACE"
        )
        
        val listing2 = ListingEntity(
            ownerUserId = savedUser1.id!!,
            placeId = savedPlace2.id!!,
            title = "3BR Roommate House in Haifa",
            price = 4500,
            autoAccept = false,
            type = "ROOMMATE_GROUP",
            capacityTotal = 3,
            spotsAvailable = 1,
            moveInDate = LocalDate.now().plusDays(30),
            rentPerRoom = 1500
        )
        
        val savedListing1 = listingRepository.save(listing1)
        val savedListing2 = listingRepository.save(listing2)
        
        println("🌱 Creating test listing photos...")
        
        // Add photos to listings
        val photo1 = ListingPhotoEntity(
            listingId = savedListing1.id!!,
            url = "https://example.com/photos/listing1_1.jpg",
            sort = 0
        )
        
        val photo2 = ListingPhotoEntity(
            listingId = savedListing1.id!!,
            url = "https://example.com/photos/listing1_2.jpg", 
            sort = 1
        )
        
        val photo3 = ListingPhotoEntity(
            listingId = savedListing2.id!!,
            url = "https://example.com/photos/listing2_1.jpg",
            sort = 0
        )
        
        listingPhotoRepository.save(photo1)
        listingPhotoRepository.save(photo2)
        listingPhotoRepository.save(photo3)
        
        println("🌱 Creating roommate group members...")
        
        // Add only the additional roommate to the group (owner is implicitly a member)
        val member2 = ListingMemberEntity(
            listingId = savedListing2.id!!,
            userId = savedUser3.id!!, // Existing roommate
            role = MemberRole.TENANT.value,
            displayOrder = 1
        )
        
        listingMemberRepository.save(member2)
        
        println("🌱 Creating test rant groups...")
        
        // Create rant group
        val rantGroup = RantGroupEntity(
            raterUserId = savedUser2.id!!,
            landlordId = savedLandlord1.id!!,
            placeId = savedPlace1.id!!,
            periodStart = LocalDate.now().minusMonths(12),
            periodEnd = LocalDate.now().minusMonths(1),
            isCurrentResidence = false,
            comment = "Great apartment overall, landlord was very responsive!"
        )
        
        val savedRantGroup = rantGroupRepository.save(rantGroup)
        
        println("🌱 DevSeederService: Test data seeding completed successfully!")
        println("🌱 Created: 3 users, 2 places, 1 landlord, 2 listings, 3 photos, 2 members, 1 rant group")
    }
}