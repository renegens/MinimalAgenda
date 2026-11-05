package data.local

import kotlinx.serialization.Serializable

@Serializable
enum class AppThemeModeEntity {
    FollowSystem, DefaultLight, DefaultDark
}