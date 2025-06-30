package com.scritch.app.categories

import androidx.compose.ui.text.intl.Locale
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

private const val CATEGORY_COLLECTION = "categories"
private const val OPTIONS_COLLECTION = "options"

class CategoryRepository {

    suspend fun getOptions(category: Category): List<OptionDto>{
        val locale = Locale.current.language
        val optionDocsLocalised = Firebase
            .firestore
            .collection(CATEGORY_COLLECTION)
            .document(category.toDbName())
            .collection("$OPTIONS_COLLECTION-$locale")
            .get()
            .documents
        val optionDocs = if (optionDocsLocalised.isEmpty()){
            Firebase
                .firestore
                .collection(CATEGORY_COLLECTION)
                .document(category.toDbName())
                .collection(OPTIONS_COLLECTION)
                .get()
                .documents
        } else {
            optionDocsLocalised
        }

        return optionDocs.map { doc ->
            OptionDto(doc)
        }
    }

    private fun Category.toDbName() = when (this){
        Category.Medium -> "medium"
        Category.Support -> "support"
        Category.Topic -> "topic"
        Category.Constraint -> "constraint"
    }
}