package com.scritch.app.userdata

import com.scritch.app.categories.Category

data class UserData(
    val disabledMediumIds: List<String>,
    val disabledSupportIds: List<String>,
    val categorySettings: Map<Category, Boolean>,
){
    companion object {
        fun fromDto(dto: UserDataDto): UserData {
            return UserData(
                disabledMediumIds = dto.disabledMediumIds ?: emptyList(),
                disabledSupportIds = dto.disabledSupportIds ?: emptyList(),
                categorySettings = dto.categorySettings?.mapKeys { entry ->
                    Category.valueOf(entry.key)
                } ?: emptyMap()
            )
        }
    }
}
