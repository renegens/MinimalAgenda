package data.local

import kotlinx.serialization.Serializable

@Serializable
enum class AppThemeEntity {
    FollowSystem, DefaultLight, DefaultDark
}