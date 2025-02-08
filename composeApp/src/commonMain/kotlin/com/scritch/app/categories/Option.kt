package com.scritch.app.categories

import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val id: String,
    val name: String,
    val selected: Boolean,
    val description: String?,
) {
    companion object {
        fun fromDto(
            dto: OptionDto,
            selected: Boolean,
        ): Option? {
            return Option(
                id = dto.id,
                name = dto.name ?: return null,
                description = dto.description,
                selected = selected,
            )
        }
    }
}
