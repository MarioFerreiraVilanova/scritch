package com.scritch.app.userdata

import com.scritch.app.categories.Category
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.map

private const val USER_DATA_COLLECTION = "user_data"

class UserDataRepository {

    suspend fun userData(userId: String): UserData {
        val doc = Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .get()

        return if (!doc.exists) {
            initialiseUserDocument(userId = userId)
            UserData.fromDto(UserDataDto(doc.reference.get()))
        } else {
            UserData.fromDto(UserDataDto(doc))
        }
    }

    fun userDataFlow(userId: String) =
        Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .snapshots()
            .map { doc ->
                if (!doc.exists) {
                    initialiseUserDocument(userId = userId)
                    UserData.fromDto(UserDataDto(doc.reference.get()))
                } else {
                    UserData.fromDto(UserDataDto(doc))
                }
            }

    suspend fun disableOptions(
        userId: String,
        category: Category,
        optionIds: List<String>,
    ) {
        Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .set(
                data = mapOf(
                    "disabled${category.name}Ids" to optionIds
                ),
                merge = true,
            )
    }

    suspend fun toggleCategory (
        userId: String,
        category: Category,
        enabled: Boolean,
    ){
        val docReference = Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)

        val dto = UserDataDto(docReference.get())
        val categorySettings = (dto.categorySettings ?: emptyMap()).toMutableMap()
        categorySettings[category.name] = enabled

        docReference.set(
            data = dto.copy(
                categorySettings = categorySettings
            ).asMap(),
        )
    }

    private suspend fun initialiseUserDocument(userId: String) {
        Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .set(UserDataDto.initial().asMap())
    }
}