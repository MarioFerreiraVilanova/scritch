package com.scritch.app.categories

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

private const val CATEGORY_COLLECTION = "categories"
private const val OPTIONS_COLLECTION = "options"

class CategoryRepository {

    suspend fun getOptions(category: Category): List<OptionDto>{
        val optionDocs = Firebase
            .firestore
            .collection(CATEGORY_COLLECTION)
            .document(category.toDbName())
            .collection(OPTIONS_COLLECTION)
            .get()
            .documents

        return optionDocs.map { doc ->
            OptionDto(doc)
        }
    }

    private fun Category.toDbName() = when (this){
        Category.Medium -> "medium"
        Category.Support -> "support"
        Category.Topic -> "topic"
    }
}