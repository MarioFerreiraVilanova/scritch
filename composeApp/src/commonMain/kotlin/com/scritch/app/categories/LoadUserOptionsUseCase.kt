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
        useFrequency: Boolean = false,
    ): List<OptionState> {
        return authenticationRepository.user()?.id?.let { userId ->
            val optionDtos = categoryRepository.getOptions(category = category)
            val userData = userDataRepository.userData(userId = userId)
            val disabledOptions = when (category) {
                Category.Medium -> userData.disabledMediumIds
                Category.Support -> userData.disabledSupportIds
                Category.Topic -> userData.disabledTopicIds
                Category.Constraint -> userData.disabledConstraintIds
            }
            val result = optionDtos.mapNotNull { dto ->
                OptionState.fromDto(
                    dto = dto,
                    selected = !disabledOptions.contains(dto.id),
                )
            }.toMutableList()

            if (useFrequency){
                optionDtos.filter { it.frequency != null }.forEach { dto ->
                    for (i in 0..(dto.frequency ?: 0)) {
                        OptionState.fromDto(
                            dto = dto,
                            selected = !disabledOptions.contains(dto.id),
                        )?.let {
                            result.add(it)
                        }
                    }
                }
            }

            result.sortedBy { it.name }
        } ?: emptyList()
    }
}