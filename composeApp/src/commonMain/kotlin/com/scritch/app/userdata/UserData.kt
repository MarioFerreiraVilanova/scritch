package com.scritch.app.userdata

import com.scritch.app.categories.Category

data class UserData(
    val disabledTopicIds: List<String>,
    val disabledMediumIds: List<String>,
    val disabledSupportIds: List<String>,
    val disabledConstraintIds: List<String>,
    val categorySettings: Map<Category, Boolean>,
){
    companion object {
        fun fromDto(dto: UserDataDto): UserData {
            return UserData(
                disabledTopicIds = dto.disabledTopicIds ?: emptyList(),
                disabledMediumIds = dto.disabledMediumIds ?: emptyList(),
                disabledSupportIds = dto.disabledSupportIds ?: emptyList(),
                disabledConstraintIds = dto.disabledConstraintIds ?: emptyList(),
                categorySettings = dto.categorySettings?.mapKeys { entry ->
                    Category.valueOf(entry.key)
                } ?: emptyMap()
            )
        }
    }
}
