package platform

/** Build-time switches supplied by the platform app (see androidApp flavors). */
interface AppConfig {
    /** True only in the FOSS build; the Play build must not show any donation link. */
    val showDonate: Boolean

    /** True only on iOS, where the Focus mode setup hint applies. */
    val showFocusModeSetup: Boolean
}
