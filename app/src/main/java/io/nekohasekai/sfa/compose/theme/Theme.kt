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

// ============================================================================
// 2026 Theme System - 静谧奢华主题
// 设计理念：沉稳内敛的色彩、精致的层次、考究的细节
// ============================================================================

private val DarkColorScheme =
    darkColorScheme(
        // Primary colors - 主色调
        primary = PremiumDarkPrimary,
        onPrimary = PremiumDarkOnPrimary,
        primaryContainer = PremiumDarkPrimaryContainer,
        onPrimaryContainer = PremiumDarkOnPrimaryContainer,
        
        // Secondary colors - 次级色调
        secondary = PremiumDarkSecondary,
        onSecondary = PremiumDarkOnSecondary,
        secondaryContainer = PremiumDarkSecondaryContainer,
        onSecondaryContainer = PremiumDarkOnSecondaryContainer,
        
        // Tertiary colors - 第三色调
        tertiary = PremiumDarkTertiary,
        onTertiary = PremiumDarkOnTertiary,
        
        // Background and surface - 背景与表面
        background = PremiumDarkBackground,
        onBackground = PremiumDarkOnBackground,
        surface = PremiumDarkSurface,
        onSurface = PremiumDarkOnSurface,
        surfaceVariant = PremiumDarkSurfaceVariant,
        onSurfaceVariant = PremiumDarkOnSurfaceVariant,
        
        // Surface container hierarchy - 表面容器层次
        // 由浅到深，用于区分卡片和容器的层级
        surfaceDim = Color(0xFF0A0A10),             // 最暗表面
        surfaceBright = Color(0xFF1F1F2E),          // 较亮表面
        surfaceContainerLowest = Color(0xFF08080C), // 最低容器
        surfaceContainerLow = Color(0xFF101018),    // 低容器
        surfaceContainer = Color(0xFF14141F),       // 标准容器
        surfaceContainerHigh = Color(0xFF1A1A28),   // 高容器
        surfaceContainerHighest = Color(0xFF222230),// 最高容器
        
        // Inverse colors - 反转色
        inverseSurface = Color(0xFFE8E6F2),
        inverseOnSurface = Color(0xFF1A1A24),
        inversePrimary = Color(0xFF5555AA),
        
        // Error colors - 错误色
        error = PremiumDarkError,
        onError = PremiumDarkOnError,
        errorContainer = Color(0xFF4A1A1A),
        onErrorContainer = Color(0xFFF2D0D0),
        
        // Outline colors - 边框色
        outline = PremiumDarkOutline,
        outlineVariant = Color(0xFF3D3D4A),
        
        // Scrim - 遮罩色
        scrim = Color(0xFF000000),
    )

private val LightColorScheme =
    lightColorScheme(
        // Primary colors - 主色调
        primary = PremiumLightPrimary,
        onPrimary = PremiumLightOnPrimary,
        primaryContainer = PremiumLightPrimaryContainer,
        onPrimaryContainer = PremiumLightOnPrimaryContainer,
        
        // Secondary colors - 次级色调
        secondary = PremiumLightSecondary,
        onSecondary = PremiumLightOnSecondary,
        secondaryContainer = PremiumLightSecondaryContainer,
        onSecondaryContainer = PremiumLightOnSecondaryContainer,
        
        // Tertiary colors - 第三色调
        tertiary = PremiumLightTertiary,
        onTertiary = PremiumLightOnTertiary,
        
        // Background and surface - 背景与表面
        background = PremiumLightBackground,
        onBackground = PremiumLightOnBackground,
        surface = PremiumLightSurface,
        onSurface = PremiumLightOnSurface,
        surfaceVariant = PremiumLightSurfaceVariant,
        onSurfaceVariant = PremiumLightOnSurfaceVariant,
        
        // Surface container hierarchy - 表面容器层次
        surfaceDim = Color(0xFFE8E8F0),             // 最暗表面
        surfaceBright = PremiumLightSurface,        // 较亮表面
        surfaceContainerLowest = Color(0xFFFFFFFF), // 最低容器
        surfaceContainerLow = Color(0xFFF8F8FC),    // 低容器
        surfaceContainer = Color(0xFFF0F0F8),       // 标准容器
        surfaceContainerHigh = Color(0xFFE8E8F0),   // 高容器
        surfaceContainerHighest = Color(0xFFE0E0E8),// 最高容器
        
        // Inverse colors - 反转色
        inverseSurface = Color(0xFF1A1A24),
        inverseOnSurface = Color(0xFFF0F0F8),
        inversePrimary = Color(0xFF9999FF),
        
        // Error colors - 错误色
        error = PremiumLightError,
        onError = PremiumLightOnError,
        errorContainer = Color(0xFFF2D0D0),
        onErrorContainer = Color(0xFF4A1A1A),
        
        // Outline colors - 边框色
        outline = PremiumLightOutline,
        outlineVariant = Color(0xFFD0D0D8),
        
        // Scrim - 遮罩色
        scrim = Color(0xFF000000),
    )

@Composable
fun Theme(
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

// ============================================================================
// Theme Extensions - 主题扩展
// ============================================================================

/**
 * 获取当前主题的卡片阴影颜色
 */
val isInDarkTheme: Boolean
    @Composable
    get() = isSystemInDarkTheme()

/**
 * 卡片表面色（带透明度，用于玻璃拟态效果）
 */
val glassSurfaceColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0xCC14141F)  // 80% 透明度
    } else {
        Color(0xCCFFFFFF)  // 80% 透明度
    }

/**
 * 边框色（用于玻璃拟态边框）
 */
val glassBorderColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0x336B687A)  // 20% 透明度
    } else {
        Color(0x33A8A8B8)  // 20% 透明度
    }
