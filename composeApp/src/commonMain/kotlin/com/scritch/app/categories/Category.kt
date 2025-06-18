package com.scritch.app.categories

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.pluralStringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.category_art_medium
import scritch.composeapp.generated.resources.category_constraint
import scritch.composeapp.generated.resources.category_support
import scritch.composeapp.generated.resources.category_topic

@Serializable
enum class Category {
    Topic,
    Medium,
    Support,
    Constraint,
}

@Composable
fun Category.toTitle(
    plural: Boolean = false,
) = when (this) {
    Category.Medium -> pluralStringResource(
        resource = Res.plurals.category_art_medium,
        quantity = if (plural) 10 else 1,
    )
    Category.Support -> pluralStringResource(
        resource = Res.plurals.category_support,
        quantity = if (plural) 10 else 1,
    )
    Category.Topic -> pluralStringResource(
        resource = Res.plurals.category_topic,
        quantity = if (plural) 10 else 1,
    )
    Category.Constraint -> pluralStringResource(
        resource = Res.plurals.category_constraint,
        quantity = if (plural) 10 else 1,
    )
}