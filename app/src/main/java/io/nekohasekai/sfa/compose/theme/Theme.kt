package io.nekohasekai.sfa.compose.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = PremiumDarkPrimary,
        onPrimary = PremiumDarkOnPrimary,
        primaryContainer = PremiumDarkPrimaryContainer,
        onPrimaryContainer = PremiumDarkOnPrimaryContainer,
        secondary = PremiumDarkSecondary,
        onSecondary = PremiumDarkOnSecondary,
        secondaryContainer = PremiumDarkSecondaryContainer,
        onSecondaryContainer = PremiumDarkOnSecondaryContainer,
        tertiary = PremiumDarkTertiary,
        onTertiary = PremiumDarkOnTertiary,
        background = PremiumDarkBackground,
        onBackground = PremiumDarkOnBackground,
        surface = PremiumDarkSurface,
        onSurface = PremiumDarkOnSurface,
        surfaceVariant = PremiumDarkSurfaceVariant,
        onSurfaceVariant = PremiumDarkOnSurfaceVariant,
        // Detailed Surface slots for Premium Look
        surfaceDim = PremiumDarkSurface,
        surfaceBright = Color(0xFF242A3D),
        surfaceContainerLowest = Color(0xFF0A0C14),
        surfaceContainerLow = Color(0xFF161B29),
        surfaceContainer = Color(0xFF1C2235),
        surfaceContainerHigh = Color(0xFF242A3D),
        surfaceContainerHighest = Color(0xFF2E354C),
        error = PremiumDarkError,
        onError = PremiumDarkOnError,
        outline = PremiumDarkOutline,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PremiumLightPrimary,
        onPrimary = PremiumLightOnPrimary,
        primaryContainer = PremiumLightPrimaryContainer,
        onPrimaryContainer = PremiumLightOnPrimaryContainer,
        secondary = PremiumLightSecondary,
        onSecondary = PremiumLightOnSecondary,
        secondaryContainer = PremiumLightSecondaryContainer,
        onSecondaryContainer = PremiumLightOnSecondaryContainer,
        tertiary = PremiumLightTertiary,
        onTertiary = PremiumLightOnTertiary,
        background = PremiumLightBackground,
        onBackground = PremiumLightOnBackground,
        surface = PremiumLightSurface,
        onSurface = PremiumLightOnSurface,
        surfaceVariant = PremiumLightSurfaceVariant,
        onSurfaceVariant = PremiumLightOnSurfaceVariant,
        // Detailed Surface slots for Premium Look
        surfaceDim = Color(0xFFDEDEE7),
        surfaceBright = PremiumLightSurface,
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF0F2F9),
        surfaceContainer = Color(0xFFE6E8EF),
        surfaceContainerHigh = Color(0xFFDEDFE6),
        surfaceContainerHighest = Color(0xFFD3D4DB),
        error = PremiumLightError,
        onError = PremiumLightOnError,
        outline = PremiumLightOutline,
    )

@Composable
fun SFATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Disabled by default to enforce Premium Design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= 31 -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb() // Use background color for smoother look
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
