# 2026 UI Design System - sing-box-for-android

### 核心原则

1. **内敛沉稳** - 使用温润的中性色调，避免过于鲜艳的霓虹色
2. **精致细节** - 考究的字间距、行高和圆角
3. **柔和过渡** - 精致的动效和渐变
4. **层次分明** - 清晰的表面层级系统

---

## 色彩系统

### 深色主题 (Dark Theme)

| 名称 | 色值 | 用途 |
|------|------|------|
| Primary | `#9999FF` | 柔和薰衣草紫 - 主要强调色 |
| Secondary | `#8EC8C8` | 静谧青瓷 - 次级强调色 |
| Tertiary | `#D4A574` | 温润琥珀 - 第三强调色 |
| Background | `#0D0D14` | 星空黑 - 页面背景 |
| Surface | `#14141F` | 深紫夜 - 卡片背景 |
| SurfaceVariant | `#1F1F2E` | 暗紫灰 - 次级容器 |

### 浅色主题 (Light Theme)

| 名称 | 色值 | 用途 |
|------|------|------|
| Primary | `#5555AA` | 深薰衣草紫 - 主要强调色 |
| Secondary | `#4A8888` | 稳重青瓷 - 次级强调色 |
| Tertiary | `#9A7744` | 稳重琥珀 - 第三强调色 |
| Background | `#FAFAFC` | 暖灰白 - 页面背景 |
| Surface | `#FFFFFE` | 象牙白 - 卡片背景 |
| SurfaceVariant | `#F0F0F8` | 浅紫灰 - 次级容器 |

### 语义色彩

| 名称 | 色值 | 用途 |
|------|------|------|
| ServiceRunning | `#6BBF6B` | 柔和草绿 - 运行中状态 |
| ServiceStopped | `#8A8A9A` | 温和灰 - 停止状态 |
| ServiceError | `#D46B6B` | 柔和红 - 错误状态 |
| WarningOrange | `#D4A574` | 琥珀黄 - 警告状态 |

---

## 字体系统

### 规格

| 样式 | 字号 | 字重 | 用途 |
|------|------|------|------|
| displayLarge | 56sp | Light | 超大标题 |
| headlineSmall | 22sp | Medium | 页面标题 |
| titleMedium | 15sp | SemiBold | 卡片标题 |
| bodyLarge | 15sp | Normal | 正文 |
| labelLarge | 13sp | SemiBold | 按钮/标签 |
| labelSmall | 10sp | SemiBold | 辅助文本 |

### 设计原则

- 使用 **SemiBold** 而非 Bold 作为强调字重
- 标题使用负字间距 (letterSpacing: -0.5sp)
- 正文使用正字间距 (letterSpacing: 0.4sp)
- 行高比字号大约 1.5 倍

---

## 形状系统

| 类型 | 圆角 | 用途 |
|------|------|------|
| extraSmall | 6dp | 按钮、芯片 |
| small | 12dp | 输入框、小卡片 |
| medium | 20dp | 标准卡片 |
| large | 28dp | 底部表单 |
| extraLarge | 36dp | 模态对话框 |

### 特殊形状

- **StatusBar**: 24dp 圆角
- **FAB**: 16dp 圆角
- **ActionChip**: 14dp 圆角

---

## 组件库

### 卡片组件

1. **PremiumCard** - 基础高级卡片
2. **PremiumCardWithTitle** - 带标题的卡片
3. **StatCard** - 统计数值卡片
4. **GlassCard** - 玻璃拟态卡片
5. **ActionCard** - 可点击操作卡片

### 功能卡片

1. **ProfilesCard** - 配置文件卡片
2. **ClashModeCard** - 模式选择卡片
3. **DownloadTrafficCard** - 下载流量卡片
4. **UploadTrafficCard** - 上传流量卡片
5. **DebugCard** - 调试信息卡片
6. **ConnectionsCard** - 连接信息卡片

### 状态组件

1. **ServiceStatusBar** - 服务状态栏（带呼吸动效）
2. **StatusIndicator** - 状态指示灯
3. **UptimeText** - 运行时间显示

---

## 动效规范

### 呼吸动效 (StatusIndicator)

```kotlin
animationSpec = infiniteRepeatable(
    animation = tween(
        durationMillis = 2000,  // 慢节奏
        easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    ),
    repeatMode = RepeatMode.Reverse
)
```

### 入场动效

```kotlin
enter = slideInVertically(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
) + fadeIn(animationSpec = tween(300))
```

### 内容尺寸变化

```kotlin
animateContentSize(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

---

## 间距规范

| 名称 | 数值 | 用途 |
|------|------|------|
| xs | 4dp | 紧凑元素间距 |
| sm | 6dp | 小元素间距 |
| md | 12dp | 标准间距 |
| lg | 16dp | 卡片内边距 |
| xl | 20dp | 大间距 |
| xxl | 28dp | 区块间距 |

---

## 阴影规范

| 层级 | elevation | 用途 |
|------|-----------|------|
| 0 | 0dp | 卡片默认（无阴影） |
| 1 | 4dp | 轻微抬升 |
| 2 | 6dp | 悬浮卡片 |
| 3 | 8dp | 状态栏 |

---

## 使用示例

### 创建统计卡片

```kotlin
StatCard(
    label = "下载速度",
    value = "12.5 MB/s",
    icon = Icons.Rounded.ArrowDownward,
    subValue = "总计: 1.2 GB",
    iconTint = MaterialTheme.colorScheme.secondary,
)
```

### 创建带标题卡片

```kotlin
PremiumCardWithTitle(
    title = "配置文件",
    icon = Icons.Outlined.Description,
) {
    // 卡片内容
}
```

---

## 文件结构

```
compose/
├── theme/
│   ├── Color.kt        # 色彩定义
│   ├── Type.kt         # 字体定义
│   ├── Shape.kt        # 形状定义
│   └── Theme.kt        # 主题组合
├── component/
│   ├── PremiumCard.kt  # 高级卡片组件
│   └── ServiceStatusBar.kt # 状态栏组件
└── screen/
    └── dashboard/
        ├── ProfilesCard.kt
        ├── ClashModeCard.kt
        ├── TrafficCard.kt
        └── ...
```

---

## 版本历史

### v2026.1 (2026-02-06)
- 初始设计系统建立
- 实现「静谧奢华」配色方案
- 升级字体排版系统
- 增加精致动效
- 创建可复用组件库
