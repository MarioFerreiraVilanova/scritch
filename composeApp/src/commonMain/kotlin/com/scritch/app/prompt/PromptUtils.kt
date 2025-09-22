package com.scritch.app.prompt

import com.scritch.app.categories.OptionState

fun buildPromptString(
    topic: OptionState?,
    support: OptionState?,
    medium: OptionState?,
    constraint: OptionState?,
): String? {
    if (medium == null && support == null) return null

    val builder = StringBuilder()

    if (topic?.prompt != null) {
        val text = topic.prompt.replace("*", "")
        builder.append(text)

        val suffix = when {
            support?.prompt != null || medium?.prompt != null -> ", "
            constraint?.prompt != null -> ". "
            else -> "."
        }
        builder.append(suffix)
    }

    if (support?.prompt != null) {
        val text = support.prompt.replace("*", "")
        builder.append(text)

        val suffix = when {
            medium?.prompt != null -> ", "
            constraint?.prompt != null -> ". "
            else -> "."
        }
        builder.append(suffix)
    }

    if (medium?.prompt != null) {
        val text = medium.prompt.replace("*", "")
        builder.append(text)

        val suffix = when {
            constraint?.prompt != null -> ". "
            else -> "."
        }
        builder.append(suffix)
    }

    if (constraint?.prompt != null) {
        val text = constraint.prompt.replace("*", "")
        builder.append(text)
        builder.append(".")
    }

    return builder.toString()
}