package com.scritch.app.userdata

data class UserData(
    val disabledMediumIds: List<String>,
    val disabledSupportIds: List<String>,
){
    companion object {
        fun fromDto(dto: UserDataDto): UserData {
            return UserData(
                disabledMediumIds = dto.disabledMediumIds ?: emptyList(),
                disabledSupportIds = dto.disabledSupportIds ?: emptyList(),
            )
        }
    }
}
