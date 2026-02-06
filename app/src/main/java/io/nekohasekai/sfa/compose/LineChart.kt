package io.nekohasekai.sfa.compose

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun LineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    animate: Boolean = true,
) {
    val animationProgress = remember { Animatable(if (animate) 0f else 1f) }

    LaunchedEffect(data) {
        if (animate) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300),
            )
        }
    }

    Canvas(
        modifier =
        modifier
            .fillMaxWidth()
            .height(80.dp),
    ) {
        val width = size.width
        val height = size.height
        val maxValue = max(data.maxOrNull() ?: 1f, 1f) * 1.2f // Add 20% padding
        val pointCount = data.size

        // Draw horizontal grid lines
        val gridLineCount = 3
        for (i in 0..gridLineCount) {
            val y = height * i / gridLineCount
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            )
        }

        if (pointCount > 1) {
            val path = Path()
            val spacing = width / (pointCount - 1).toFloat()

            // Calculate points
            val points =
                data.mapIndexed { index, value ->
                    val x = index * spacing
                    val normalizedValue = (value / maxValue).coerceIn(0f, 1f)
                    val y = height * (1 - normalizedValue)
                    Offset(x, y)
                }

            // Build the path with Bezier curves for smoothness
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val progress = if (animate) animationProgress.value else 1f
                val pointIndex = (i * progress).toInt().coerceAtMost(points.size - 1)

                if (i <= pointIndex) {
                    val prev = points[i - 1]
                    val current = points[i]
                    val controlX = (prev.x + current.x) / 2
                    path.cubicTo(
                        controlX, prev.y,
                        controlX, current.y,
                        current.x, current.y
                    )
                }
            }

            // Draw the line
            drawPath(
                path = path,
                color = lineColor,
                style =
                Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )

            // Draw gradient fill under the line
            val fillPath = Path()
            fillPath.addPath(path)

            // Complete the fill area
            if (points.isNotEmpty()) {
                val progressIndex = ((points.size - 1) * animationProgress.value).toInt().coerceAtMost(points.size - 1)
                val lastPoint = points[progressIndex]

                fillPath.lineTo(lastPoint.x, height)
                fillPath.lineTo(0f, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.25f),
                            lineColor.copy(alpha = 0.0f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )
            }
        }
    }
}
