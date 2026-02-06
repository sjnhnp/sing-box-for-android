package io.nekohasekai.sfa.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max

// ============================================================================
// 2026 Line Chart - 精致折线图
// 设计理念：柔和的曲线、优雅的渐变、细腻的网格
// ============================================================================

@Composable
fun LineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    animate: Boolean = true,
    showGrid: Boolean = true,
    lineWidth: Float = 2f,
    fillAlpha: Float = 0.2f,
) {
    val animationProgress = remember { Animatable(if (animate) 0f else 1f) }

    LaunchedEffect(data) {
        if (animate) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                ),
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
    ) {
        val width = size.width
        val height = size.height
        val maxValue = max(data.maxOrNull() ?: 1f, 1f) * 1.15f // 15% 上边距
        val pointCount = data.size

        // 绘制水平网格线（更细腻）
        if (showGrid) {
            val gridLineCount = 2  // 减少网格线数量，更简洁
            for (i in 1 until gridLineCount + 1) {
                val y = height * i / (gridLineCount + 1)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 0.5.dp.toPx(),  // 更细的网格线
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6f, 8f),  // 更小的虚线间隔
                        0f
                    ),
                )
            }
        }

        if (pointCount > 1) {
            val path = Path()
            val spacing = width / (pointCount - 1).toFloat()

            // 计算数据点
            val points = data.mapIndexed { index, value ->
                val x = index * spacing
                val normalizedValue = (value / maxValue).coerceIn(0f, 1f)
                val y = height * (1 - normalizedValue)
                Offset(x, y)
            }

            // 使用贝塞尔曲线构建平滑路径
            path.moveTo(points[0].x, points[0].y)
            
            val progress = if (animate) animationProgress.value else 1f
            val visiblePointCount = ((points.size - 1) * progress).toInt() + 1
            
            for (i in 1 until minOf(visiblePointCount, points.size)) {
                val prev = points[i - 1]
                val current = points[i]
                
                // 更平滑的贝塞尔曲线控制点
                val controlX1 = prev.x + (current.x - prev.x) * 0.4f
                val controlX2 = prev.x + (current.x - prev.x) * 0.6f
                
                path.cubicTo(
                    controlX1, prev.y,
                    controlX2, current.y,
                    current.x, current.y
                )
            }

            // 绘制渐变填充区域（先绘制，在线条下方）
            if (points.isNotEmpty() && fillAlpha > 0) {
                val fillPath = Path()
                fillPath.addPath(path)
                
                val lastVisibleIndex = minOf(visiblePointCount - 1, points.size - 1)
                val lastPoint = points[lastVisibleIndex]

                fillPath.lineTo(lastPoint.x, height)
                fillPath.lineTo(0f, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = fillAlpha),
                            lineColor.copy(alpha = fillAlpha * 0.3f),
                            lineColor.copy(alpha = 0f),
                        ),
                        startY = 0f,
                        endY = height
                    )
                )
            }

            // 绘制主线条
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = lineWidth.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )

            // 绘制线条发光效果（微妙的光晕）
            drawPath(
                path = path,
                color = lineColor.copy(alpha = 0.3f),
                style = Stroke(
                    width = (lineWidth + 2f).dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

/**
 * 迷你折线图 - 用于紧凑空间
 */
@Composable
fun MiniLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
) {
    LineChart(
        data = data,
        modifier = modifier.height(32.dp),
        lineColor = lineColor,
        animate = false,
        showGrid = false,
        lineWidth = 1.5f,
        fillAlpha = 0.15f,
    )
}
