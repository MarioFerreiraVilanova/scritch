package com.scritch.app.categories

import kotlinx.serialization.Serializable

@Serializable
data class OptionState(
    val id: String,
    val name: String,
    val selected: Boolean,
    val description: String?,
    val tips: String?,
    val enabled: Boolean = true,
) {
    companion object {
        fun fromDto(
            dto: OptionDto,
            selected: Boolean,
        ): OptionState? {
            return OptionState(
                id = dto.id,
                name = dto.name ?: return null,
                description = dto.description,
                tips = dto.tips,
                selected = selected,
            )
        }
    }
}
