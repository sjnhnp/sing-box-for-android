package io.nekohasekai.sfa.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// 2026 Shape System - 柔和圆润的形状系统
// 设计理念：更大的圆角营造柔和质感，避免生硬的边角
// ============================================================================

val Shapes =
    Shapes(
        // 用于小按钮、芯片、标签等
        extraSmall = RoundedCornerShape(6.dp),      // 从4dp增加到6dp
        
        // 用于输入框、小卡片等
        small = RoundedCornerShape(12.dp),          // 从8dp增加到12dp
        
        // 用于卡片、对话框等
        medium = RoundedCornerShape(20.dp),         // 从16dp增加到20dp
        
        // 用于底部表单、大型容器等
        large = RoundedCornerShape(28.dp),          // 从24dp增加到28dp
        
        // 用于全屏模态、抽屉等
        extraLarge = RoundedCornerShape(36.dp),     // 从32dp增加到36dp
    )

// ============================================================================
// Extended Shapes - 扩展形状
// ============================================================================

// 状态栏浮动卡片形状 - 更圆润的胶囊形
val StatusBarShape = RoundedCornerShape(24.dp)

// FAB形状 - 保持经典圆形
val FABShape = RoundedCornerShape(16.dp)

// 底部导航栏指示器形状
val NavIndicatorShape = RoundedCornerShape(8.dp)

// 选项卡/分段按钮形状
val SegmentedButtonShape = RoundedCornerShape(12.dp)

// 徽章/Badge形状
val BadgeShape = RoundedCornerShape(6.dp)

// 进度条圆角
val ProgressBarShape = RoundedCornerShape(4.dp)

// 输入框形状
val TextFieldShape = RoundedCornerShape(14.dp)

// 下拉菜单形状
val DropdownShape = RoundedCornerShape(16.dp)

// 工具提示形状
val TooltipShape = RoundedCornerShape(8.dp)

// 头像形状（圆形）
val AvatarShape = RoundedCornerShape(50)

// 卡片悬浮效果形状
val ElevatedCardShape = RoundedCornerShape(20.dp)

// 紧凑型卡片形状
val CompactCardShape = RoundedCornerShape(14.dp)
