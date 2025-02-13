package com.scritch.app.categories

import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.userdata.UserDataRepository

class LoadUserOptionsUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val userDataRepository: UserDataRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(
        category: Category,
    ): List<OptionState> {
        return authenticationRepository.user()?.id?.let { userId ->
            val optionDtos = categoryRepository.getOptions(category = category)
            val userData = userDataRepository.userData(userId = userId)
            val disabledOptions = when (category) {
                Category.Medium -> userData.disabledMediumIds
                Category.Support -> userData.disabledSupportIds
            }
            optionDtos.mapNotNull { dto ->
                OptionState.fromDto(
                    dto = dto,
                    selected = !disabledOptions.contains(dto.id),
                )
            }.sortedBy { it.name }
        } ?: emptyList()
    }
}