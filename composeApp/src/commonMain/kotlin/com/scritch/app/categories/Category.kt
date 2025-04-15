package com.scritch.app.categories

import kotlinx.serialization.Serializable

@Serializable
enum class Category {
    Topic,
    Medium,
    Support,
    Constraint,
}