package com.scritch.app.userprofile

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.map
import kotlin.random.Random

private const val USER_PROFILES_COLLECTION = "user_profiles"

private val ARTIST_NAMES = listOf(
    "Picasso", "VanGogh", "Monet", "Dali", "Frida", "Warhol", "Pollock",
    "Renoir", "Degas", "Cezanne", "Klimt", "Matisse", "Kandinsky", "Rothko",
    "Basquiat", "OKeeffe", "Hockney", "Bacon", "Koons", "Banksy", "Kusama",
    "Leonardo", "Michelangelo", "Raphael", "Caravaggio", "Rembrandt", "Vermeer",
    "Goya", "Manet", "Toulouse", "Seurat", "Gauguin", "Munch", "Schiele",
    "Hopper", "Klee", "Mondrian", "Magritte", "Chagall", "Miro", "Duchamp",
    "Weston", "Adams", "Bourgeois", "Sherman", "Haring", "Lichtenstein",
    "Johns", "Rauschenberg", "Stella", "Twombly"
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