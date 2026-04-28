package io.nekohasekai.sfa.update

enum class UpdateTrack {
    STABLE,
    BETA,
    ALPHA,
    ;

    companion object {
        fun fromString(value: String): UpdateTrack = when (value.lowercase()) {
            "beta" -> BETA
            "alpha" -> ALPHA
            else -> STABLE
        }
    }
}
