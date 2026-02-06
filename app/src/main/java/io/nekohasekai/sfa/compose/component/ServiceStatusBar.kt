package io.nekohasekai.sfa.compose.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.nekohasekai.sfa.R
import io.nekohasekai.sfa.compose.theme.ServiceRunning
import io.nekohasekai.sfa.compose.theme.ServiceStopped
import io.nekohasekai.sfa.compose.theme.ServiceError
import io.nekohasekai.sfa.compose.theme.WarningOrange
import io.nekohasekai.sfa.compose.theme.StatusBarShape
import io.nekohasekai.sfa.constant.Status
import kotlinx.coroutines.delay

// ============================================================================
// 2026 Service Status Bar - 精致状态栏
// 设计理念：柔和的玻璃拟态效果、精致的呼吸动效、考究的细节
// ============================================================================

@Composable
fun ServiceStatusBar(
    visible: Boolean,
    serviceStatus: Status,
    startTime: Long?,
    groupsCount: Int,
    hasGroups: Boolean,
    onGroupsClick: () -> Unit,
    connectionsCount: Int,
    onConnectionsClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 柔和的呼吸动效（更慢、更细腻）
    val infiniteTransition = rememberInfiniteTransition(label = "BreathingTransition")
    
    // 呼吸光晕透明度
    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,  // 更慢的呼吸节奏
                easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathAlpha"
    )
    
    // 呼吸光晕缩放
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,  // 更小的缩放范围
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(150)),
        modifier = modifier,
    ) {
        // 玻璃拟态容器
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(StatusBarShape),
            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            shape = StatusBarShape,
        ) {
            // 添加微妙的渐变边框
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.05f),
                                )
                            ),
                            cornerRadius = CornerRadius(24.dp.toPx()),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 状态指示灯（带呼吸效果）
                    StatusIndicator(
                        status = serviceStatus,
                        breatheAlpha = breatheAlpha,
                        breatheScale = breatheScale,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    // 状态文本
                    StatusLabel(
                        status = serviceStatus,
                        modifier = Modifier.weight(1f),
                    )

                    // 连接数按钮
                    ActionChip(
                        count = connectionsCount,
                        icon = Icons.Outlined.Cable,
                        contentDescription = stringResource(R.string.title_connections),
                        onClick = onConnectionsClick,
                    )

                    // 分组按钮
                    if (hasGroups) {
                        ActionChip(
                            count = groupsCount,
                            icon = Icons.Default.Folder,
                            contentDescription = stringResource(R.string.title_groups),
                            onClick = onGroupsClick,
                        )
                    }

                    // 停止按钮
                    StopButton(
                        startTime = startTime,
                        onClick = onStopClick,
                    )
                }
            }
        }
    }
}

/**
 * 状态指示灯组件 - 带呼吸动效
 */
@Composable
private fun StatusIndicator(
    status: Status,
    breatheAlpha: Float,
    breatheScale: Float,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (status) {
        Status.Started -> ServiceRunning
        Status.Starting -> WarningOrange
        Status.Stopping -> ServiceError
        else -> ServiceStopped
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(24.dp)
    ) {
        // 外层呼吸光晕（仅运行状态显示）
        if (status == Status.Started) {
            Box(
                modifier = Modifier
                    .size((10.dp.value * breatheScale).dp)
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.4f * (1f - breatheAlpha)))
            )
        }
        
        // 内层指示点
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(statusColor)
        )
    }
}

/**
 * 状态标签组件
 */
@Composable
private fun StatusLabel(
    status: Status,
    modifier: Modifier = Modifier,
) {
    Text(
        text = when (status) {
            Status.Starting -> stringResource(R.string.status_starting)
            Status.Started -> stringResource(R.string.status_started)
            Status.Stopping -> stringResource(R.string.status_stopping)
            else -> ""
        },
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

/**
 * 操作芯片按钮
 */
@Composable
private fun ActionChip(
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        modifier = Modifier.height(36.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

/**
 * 停止按钮组件
 */
@Composable
private fun StopButton(
    startTime: Long?,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.height(36.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (startTime != null) {
                UptimeText(startTime = startTime)
            }
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = stringResource(R.string.stop),
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * 运行时间显示组件
 */
@Composable
fun UptimeText(startTime: Long, modifier: Modifier = Modifier) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(startTime) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val elapsedSeconds = ((currentTime - startTime) / 1000).coerceAtLeast(0)
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60

    val formattedTime =
        if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }

    Text(
        text = formattedTime,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier,
    )
}
