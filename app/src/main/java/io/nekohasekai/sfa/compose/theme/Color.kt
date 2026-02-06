package io.nekohasekai.sfa.compose.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// 2026 "Quiet Luxury" Design System - 静谧奢华配色
// 设计理念：不花哨但深厚品质，采用内敛沉稳的高级色调
// ============================================================================

// Premium Dark Palette - 深邃夜空
// 使用更温润、内敛的色调，避免过于鲜艳的霓虹色
val PremiumDarkPrimary = Color(0xFF9999FF)         // 柔和薰衣草紫
val PremiumDarkOnPrimary = Color(0xFF1A1A2E)       // 深邃靛蓝
val PremiumDarkPrimaryContainer = Color(0xFF3D3D6B) // 暮光紫容器
val PremiumDarkOnPrimaryContainer = Color(0xFFE8E8FF) // 淡紫白
val PremiumDarkSecondary = Color(0xFF8EC8C8)       // 静谧青瓷（替代Electric Cyan）
val PremiumDarkOnSecondary = Color(0xFF1A2E2E)     // 深青
val PremiumDarkSecondaryContainer = Color(0xFF2E4A4A) // 墨绿容器
val PremiumDarkOnSecondaryContainer = Color(0xFFD0EFEF) // 淡青白
val PremiumDarkTertiary = Color(0xFFD4A574)        // 温润琥珀（更柔和的橙）
val PremiumDarkOnTertiary = Color(0xFF3D2E1A)      // 深棕
val PremiumDarkBackground = Color(0xFF0D0D14)      // 星空黑
val PremiumDarkOnBackground = Color(0xFFE8E6F2)    // 月白
val PremiumDarkSurface = Color(0xFF14141F)         // 深紫夜
val PremiumDarkOnSurface = Color(0xFFE8E6F2)       // 月白
val PremiumDarkSurfaceVariant = Color(0xFF1F1F2E)  // 暗紫灰
val PremiumDarkOnSurfaceVariant = Color(0xFFB8B5C8) // 灰紫
val PremiumDarkOutline = Color(0xFF6B687A)         // 暗边框
val PremiumDarkError = Color(0xFFE8A0A0)           // 柔和红
val PremiumDarkOnError = Color(0xFF4A1010)         // 深红

// Premium Light Palette - 晨曦白昼
// 采用温暖的灰白色调，避免刺眼的纯白
val PremiumLightPrimary = Color(0xFF5555AA)        // 深薰衣草紫
val PremiumLightOnPrimary = Color(0xFFFAFAFF)      // 纯白
val PremiumLightPrimaryContainer = Color(0xFFE8E8FF) // 淡紫容器
val PremiumLightOnPrimaryContainer = Color(0xFF2A2A4A) // 暮紫
val PremiumLightSecondary = Color(0xFF4A8888)      // 稳重青瓷
val PremiumLightOnSecondary = Color(0xFFFAFAFF)    // 纯白
val PremiumLightSecondaryContainer = Color(0xFFD8F0F0) // 淡青容器
val PremiumLightOnSecondaryContainer = Color(0xFF1A3030) // 深青
val PremiumLightTertiary = Color(0xFF9A7744)       // 稳重琥珀
val PremiumLightOnTertiary = Color(0xFFFAFAFF)     // 纯白
val PremiumLightBackground = Color(0xFFFAFAFC)     // 暖灰白
val PremiumLightOnBackground = Color(0xFF1A1A24)   // 深紫灰
val PremiumLightSurface = Color(0xFFFFFFFE)        // 象牙白
val PremiumLightOnSurface = Color(0xFF1A1A24)      // 深紫灰
val PremiumLightSurfaceVariant = Color(0xFFF0F0F8) // 浅紫灰
val PremiumLightOnSurfaceVariant = Color(0xFF3D3D4A) // 暗紫灰
val PremiumLightOutline = Color(0xFFA8A8B8)        // 灰边框
val PremiumLightError = Color(0xFFB03030)          // 沉稳红
val PremiumLightOnError = Color(0xFFFAFAFF)        // 纯白

// Legacy/Compat Colors
val SingBoxPrimary = PremiumLightPrimary
val SingBoxPrimaryDark = PremiumLightPrimaryContainer
val SingBoxPrimaryLight = PremiumLightSecondary

// ============================================================================
// Service Status Colors - 更柔和的状态色
// ============================================================================
val ServiceRunning = Color(0xFF6BBF6B)             // 柔和草绿
val ServiceStopped = Color(0xFF8A8A9A)             // 温和灰
val ServiceError = Color(0xFFD46B6B)               // 柔和红
val ServiceStarting = Color(0xFFD4A574)            // 琥珀黄（与Tertiary一致）

// ============================================================================
// Log Colors - 日志高亮色（保持可读性的同时更柔和）
// ============================================================================
val LogRed = Color(0xFFE85B7A)                     // 柔和玫红
val LogGreen = Color(0xFF6BC88F)                   // 柔和绿
val LogYellow = Color(0xFFD4C46B)                  // 柔和金黄
val LogBlue = Color(0xFF6B9AD4)                    // 柔和蓝
val LogPurple = Color(0xFFB47AD4)                  // 柔和紫
val LogRedLight = Color(0xFFD47A9A)                // 浅玫红
val LogBlueLight = Color(0xFF7AC8D4)               // 浅青蓝
val LogWhite = Color(0xFFE8E6F2)                   // 月白

// Material You seed color
val SeedColor = PremiumLightPrimary

// ============================================================================
// Semantic Colors - 语义色彩
// ============================================================================
val SuccessGreen = Color(0xFF6BBF6B)               // 成功绿
val WarningOrange = Color(0xFFD4A574)              // 警告橙
val ErrorRed = Color(0xFFD46B6B)                   // 错误红
val InfoBlue = Color(0xFF6B9AD4)                   // 信息蓝

// ============================================================================
// Extended Surface Colors - 扩展表面色（用于卡片层次）
// ============================================================================
val SurfaceElevated = Color(0xFF1A1A28)            // 抬升表面(Dark)
val SurfaceElevatedLight = Color(0xFFF5F5FA)       // 抬升表面(Light)

// ============================================================================
// Gradient Colors - 渐变色（微妙的渐变增加质感）
// ============================================================================
val GradientPrimaryStart = Color(0xFF9999FF)       // 渐变起点
val GradientPrimaryEnd = Color(0xFF7777DD)         // 渐变终点
val GradientSecondaryStart = Color(0xFF8EC8C8)     // 次级渐变起点
val GradientSecondaryEnd = Color(0xFF6BA8A8)       // 次级渐变终点

// ============================================================================
// Overlay Colors - 遮罩层色彩（用于模态和对话框背景）
// ============================================================================
val OverlayDark = Color(0xCC0D0D14)                // 深色遮罩 (80%透明)
val OverlayLight = Color(0xCCFAFAFC)               // 浅色遮罩 (80%透明)
