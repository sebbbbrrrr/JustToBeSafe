package com.justtobesafe.app.ui.components

import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.ui.theme.DeepInk

/** No custom animation is permitted when Android's animator duration scale is disabled. */
@Composable
fun calmMotionDuration(milliseconds: Int): Int {
    val context = LocalContext.current
    val scale = remember(context) {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        }.getOrDefault(1f)
    }
    return if (scale <= 0f) 0 else milliseconds
}

@Composable
fun SoftCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) { content() }
}

@Composable
fun SmallBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = containerColor) {
        Text(
            text, Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium, color = textColor
        )
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        if (subtitle != null) Text(
            subtitle, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DestinationRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SoftCard(modifier) {
        Row(
            Modifier.fillMaxWidth().clickable(onClickLabel = "Open $title", onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { icon() }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ChecklistRing(percentage: Int, modifier: Modifier = Modifier, diameter: Dp = 112.dp) {
    val progress by animateFloatAsState(
        targetValue = percentage.coerceIn(0, 100) / 100f,
        animationSpec = tween(calmMotionDuration(240)), label = "Supply checklist progress"
    )
    val track = MaterialTheme.colorScheme.surfaceVariant
    val active = MaterialTheme.colorScheme.primary
    Box(
        modifier.size(diameter).semantics {
            contentDescription = "$percentage percent of supply items marked available"
        }, contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(track, -90f, 360f, false, topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round))
            if (progress > 0f) drawArc(active, -90f, 360f * progress, false,
                topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Text("$percentage%", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
    }
}

/**
 * A lightweight, locally bundled scenic illustration derived from the approved concept.
 * It is decorative artwork, not a map, hazard forecast or photo of the user's location.
 */
@Composable
fun CountrysideHeader(
    modifier: Modifier = Modifier,
    title: String = "A little planning goes a long way"
) {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.red < 0.25f
    Box(modifier.fillMaxWidth().height(164.dp).clip(RoundedCornerShape(18.dp))
        .background(if (dark) Color(0xFF21404C) else Color(0xFFE5F4FA))) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(com.justtobesafe.app.R.drawable.calm_countryside),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = if (dark) 0.42f else 1f,
            modifier = Modifier.fillMaxSize()
        )
        // A short horizontal scrim keeps headings legible over either appearance.
        Box(Modifier.fillMaxSize().background(
            Brush.horizontalGradient(
                listOf(
                    if (dark) Color(0xFF142C36).copy(alpha = 0.87f) else Color.White.copy(alpha = 0.86f),
                    Color.Transparent
                )
            )
        ))
        Text(
            title,
            Modifier.align(Alignment.TopStart).fillMaxWidth(0.67f)
                .padding(start = 16.dp, top = 15.dp, end = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (dark) Color.White else DeepInk
        )
    }
}

@Composable
fun SafetyNotice(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(15.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}
