package com.justtobesafe.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.ui.theme.DeepInk
import com.justtobesafe.app.ui.theme.OceanTeal

@Composable
fun SoftCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) { content() }
}

@Composable
fun SmallBadge(text: String, modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.primaryContainer) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = containerColor) {
        Text(text, Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 2)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DestinationRow(title: String, subtitle: String, icon: @Composable () -> Unit,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    SoftCard(modifier) {
        Row(
            Modifier.fillMaxWidth().clickable(onClickLabel = "Open $title", onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp))
                .background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) { icon() }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ChecklistRing(percentage: Int, modifier: Modifier = Modifier, diameter: Dp = 116.dp) {
    val progress by animateFloatAsState(
        targetValue = (percentage.coerceIn(0, 100) / 100f),
        animationSpec = tween(260), label = "Checklist progress")
    Box(modifier.size(diameter).semantics { contentDescription = "$percentage percent of supplies checklist completed" },
        contentAlignment = Alignment.Center) {
        val track = MaterialTheme.colorScheme.surfaceVariant
        val active = MaterialTheme.colorScheme.primary
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2
            val size = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(track, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = Offset(inset, inset), size = size,
                style = Stroke(stroke, cap = StrokeCap.Round))
            if (progress > 0f) drawArc(active, -90f, 360f * progress, false,
                topLeft = Offset(inset, inset), size = size,
                style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Text("$percentage%", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

/** Lightweight original vector backdrop: no network image, decoding or unreviewed source artwork. */
@Composable
fun CountrysideHeader(modifier: Modifier = Modifier, title: String = "A little planning goes a long way") {
    val dark = MaterialTheme.colorScheme.background.red < .25f
    val sky = if (dark) Color(0xFF244353) else Color(0xFFE2F2F8)
    val distant = if (dark) Color(0xFF3E6B6E) else Color(0xFFA4D0CC)
    val foreground = if (dark) Color(0xFF23534F) else Color(0xFF6FAB9A)
    Box(modifier.fillMaxWidth().height(162.dp).clip(RoundedCornerShape(20.dp))
        .background(sky)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawCircle(Color.White.copy(alpha = if (dark) .12f else .65f), radius = h * .25f,
                center = Offset(w * .8f, h * .25f))
            drawPath(Path().apply {
                moveTo(0f,h*.7f); cubicTo(w*.25f,h*.34f,w*.5f,h*.86f,w,h*.48f)
                lineTo(w,h); lineTo(0f,h); close()
            },distant)
            drawPath(Path().apply {
                moveTo(0f,h*.9f); cubicTo(w*.27f,h*.55f,w*.53f,h*.98f,w,h*.63f)
                lineTo(w,h);lineTo(0f,h);close()
            },foreground)
            val hw = w*.21f; val hh=h*.28f; val hx=w*.69f; val hy=h*.51f
            drawRoundRect(Color(0xFFFFF3D9), topLeft=Offset(hx,hy),
                size=Size(hw,hh),cornerRadius=androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
            drawPath(Path().apply { moveTo(hx-w*.03f,hy);lineTo(hx+hw/2,hy-h*.16f);
                lineTo(hx+hw+w*.03f,hy);close() },Color(0xFF42667D))
            drawRect(Color(0xFF6EA6BB), topLeft=Offset(hx+hw*.2f,hy+hh*.2f),
                size=Size(hw*.23f,hh*.25f))
            drawRect(Color(0xFF3D6573), topLeft=Offset(hx+hw*.63f,hy+hh*.38f),
                size=Size(hw*.19f,hh*.62f))
        }
        Text(title, Modifier.align(Alignment.TopStart).padding(19.dp).fillMaxWidth(.60f),
            style = MaterialTheme.typography.titleLarge, color = if (dark) Color.White else DeepInk)
    }
}

@Composable
fun SafetyNotice(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}
