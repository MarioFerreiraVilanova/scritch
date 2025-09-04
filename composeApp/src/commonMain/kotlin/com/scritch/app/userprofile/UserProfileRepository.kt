package com.scritch.app.userprofile

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.map
import kotlin.random.Random

private const val USER_PROFILES_COLLECTION = "user_profiles"

private val ARTIST_NAMES = listOf(
    "Abramović", "Aliabadi", "Bacon", "Banksy", "Basquiat", "Bird", "Bourgeois", "Chagall", "Dali",
    "Delaunay", "Duchamp", "Goya", "Goldin", "Haring", "Holzer", "Hockney", "Hopper", "Kahlo",
    "Kandinsky", "Klee", "Klimt", "Krasner", "Kusama", "Leonardo", "Magritte", "Manet", "Matisse",
    "Maar", "Miro", "Mondrian", "Monet", "Morisot", "Munch", "Neshat", "O’Keeffe",
    "Pollock", "Raphael", "Rembrandt", "Rothko", "Saint-Phalle", "Sherman", "Van Gogh",
    "Vermeer", "Warhol"
)

class UserProfileRepository {

    suspend fun userProfile(userId: String): UserProfile? {
        val doc = Firebase.firestore
            .collection(USER_PROFILES_COLLECTION)
            .document(userId)
            .get()

        return if (!doc.exists) {
            initialiseUserProfile(userId = userId)
            UserProfile.fromDto(UserProfileDto(doc.reference.get()))
        } else {
            UserProfile.fromDto(UserProfileDto(doc))
        }
    }

    fun userProfileFlow(userId: String) =
        Firebase.firestore
            .collection(USER_PROFILES_COLLECTION)
            .document(userId)
            .snapshots()
            .map { doc ->
                if (!doc.exists) {
                    initialiseUserProfile(userId = userId)
                    UserProfile.fromDto(UserProfileDto(doc.reference.get()))
                } else {
                    UserProfile.fromDto(UserProfileDto(doc))
                }
            }

    suspend fun getUserNickname(userId: String): String? {
        val doc = Firebase.firestore
            .collection(USER_PROFILES_COLLECTION)
            .document(userId)
            .get()
        
        return if (doc.exists) {
            UserProfileDto(doc).nickname
        } else {
            null
        }
    }


    private suspend fun generateUniqueNickname(): String {
        repeat(10) { // 10 attempts should be more than enough
            val nickname = "${ARTIST_NAMES.random()}${Random.nextInt(10, 999)}"
            
            // Check if any user profile already has this nickname
            val existingProfiles = Firebase.firestore
                .collection(USER_PROFILES_COLLECTION)
                .where { "nickname" equalTo nickname }
                .get()
                
            if (existingProfiles.documents.isEmpty()) {
                return nickname
            }
        }
        throw Exception("Could not generate unique nickname")
    }

    private suspend fun initialiseUserProfile(userId: String) {
        val nickname = generateUniqueNickname()
        Firebase.firestore
            .collection(USER_PROFILES_COLLECTION)
            .document(userId)
            .set(UserProfileDto.initial(userId, nickname).asMap())
    }
}