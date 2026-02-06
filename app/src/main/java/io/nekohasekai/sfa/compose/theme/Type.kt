package io.nekohasekai.sfa.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// 2026 Typography System - 精致字体排版
// 设计理念：考究的字重层次、舒适的行高、精细的字间距
// ============================================================================

// 自定义字体家族（可选，如果没有自定义字体则回退到系统默认）
// 建议使用 Google Sans、Inter 或 SF Pro 等现代字体
val DefaultFontFamily = FontFamily.Default

// Material 3 Typography with refined metrics
val Typography =
    Typography(
        // ====================================================================
        // Display Styles - 用于超大标题和醒目展示
        // ====================================================================
        displayLarge =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Light,  // 更轻的字重，优雅感
            fontSize = 56.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.5).sp,      // 更紧的字间距
        ),
        displayMedium =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 44.sp,
            lineHeight = 52.sp,
            letterSpacing = (-0.25).sp,
        ),
        displaySmall =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp,
        ),
        // ====================================================================
        // Headline Styles - 用于页面标题和分区标题
        // ====================================================================
        headlineLarge =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,  // 不使用过重的字体
            fontSize = 30.sp,                // 稍微调小，更沉稳
            lineHeight = 38.sp,
            letterSpacing = 0.sp,
        ),
        headlineMedium =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 26.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.sp,
        ),
        headlineSmall =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Medium,  // 稍重，增加层次
            fontSize = 22.sp,
            lineHeight = 30.sp,
            letterSpacing = 0.sp,
        ),
        // ====================================================================
        // Title Styles - 用于卡片标题和列表项标题
        // ====================================================================
        titleLarge =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold, // 使用SemiBold而非Bold
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
        ),
        titleMedium =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,                 // 从16调整到15，更精致
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp,
        ),
        titleSmall =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp,
        ),
        // ====================================================================
        // Body Styles - 用于正文内容
        // ====================================================================
        bodyLarge =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,                 // 从16调整到15
            lineHeight = 24.sp,               // 更大的行高，阅读更舒适
            letterSpacing = 0.4.sp,           // 略微增加字间距
        ),
        bodyMedium =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,                 // 从14调整到13
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,                 // 从12调整到11
            lineHeight = 16.sp,
            letterSpacing = 0.3.sp,
        ),
        // ====================================================================
        // Label Styles - 用于按钮、标签和小文本
        // ====================================================================
        labelLarge =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold, // SemiBold替代Medium
            fontSize = 13.sp,                 // 从14调整到13
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,                 // 从12调整到11
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        ),
        labelSmall =
        TextStyle(
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,                 // 从11调整到10
            lineHeight = 14.sp,
            letterSpacing = 0.4.sp,
        ),
    )

// ============================================================================
// Extended Typography - 扩展字体样式
// ============================================================================

// 用于数字显示（如流量统计）
val NumberDisplayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,         // 轻字重，数字更优雅
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-1).sp,               // 数字紧凑
)

val NumberDisplayMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp,
)

val NumberDisplaySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp,
)

// 用于代码/日志显示的等宽字体样式
val CodeText = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.sp,
)

val CodeTextSmall = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.sp,
)

// 用于运行时间显示
val UptimeText = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.5.sp,
)

// 用于状态栏小文本
val StatusText = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.3.sp,
)
