package app.minimal.fasting.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import app.minimal.fasting.theme.MinimalTheme

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    circleProgress: Float,
) {
    val constrainedProgress = progress.coerceIn(0f, 1f)
    val constrainedCircleProgress = circleProgress.coerceIn(0f, 1f)
    val startColor = MinimalTheme.color.typography
    val endColor = MinimalTheme.color.emphasis
    val circleColor = if (progress > circleProgress) startColor else endColor

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
    ) {
        val progressWidth = size.width * constrainedProgress
        val circlePosition = size.width * constrainedCircleProgress

        // Draw black area representing progress
        drawRect(
            color = startColor,
            size = androidx.compose.ui.geometry.Size(width = progressWidth, height = size.height)
        )

        // Draw orange area representing remaining progress
        drawRect(
            color = endColor,
            topLeft = Offset(x = progressWidth, y = 0f),
            size = androidx.compose.ui.geometry.Size(
                width = size.width - progressWidth,
                height = size.height
            )
        )

        // Draw orange circle representing secondary progress above the bar
        drawCircle(
            color = circleColor,
            radius = 8.dp.toPx(),
            center = Offset(x = circlePosition, y = -16.dp.toPx()) // Positioned 4dp above the bar
        )
    }
}