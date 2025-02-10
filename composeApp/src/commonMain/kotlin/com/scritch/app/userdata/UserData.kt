package com.scritch.app.userdata

data class UserData(
    val needsInitialSetup: Boolean,
){
    companion object {
        fun fromDto(dto: UserDataDto): UserData? {
            return UserData(
                needsInitialSetup = dto.needsInitialSetup ?: return null
            )
        }
    }
}
