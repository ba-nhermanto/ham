package com.ham.activitymonitorapp.data.entities

enum class Activity(
    val type: String,
    val intensity: Float
) {
    VERY_LIGHT("Very Light", 0.5f),
    LIGHT("Light", 0.64f),
    MODERATE("Moderate", 0.77f),
    HARD("Hard", 0.94f),
    VERY_HARD("Hard", 1f)
}