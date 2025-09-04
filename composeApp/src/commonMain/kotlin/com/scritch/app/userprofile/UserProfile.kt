package com.scritch.app.userprofile

data class UserProfile(
    val userId: String,
    val nickname: String,
) {
    companion object {
        fun fromDto(dto: UserProfileDto): UserProfile? {
            return UserProfile(
                userId = dto.userId ?: return null,
                nickname = dto.nickname ?: return null,
            )
        }
    }
}