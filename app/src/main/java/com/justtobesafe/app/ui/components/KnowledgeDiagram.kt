package com.justtobesafe.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.KnowledgeVisual

/**
 * Native, offline, scalable illustration templates: no bitmap, webview, font asset
 * or network request. Short accessible labels stay actual Android text.
 * These are schematic illustrations, not professional safety procedure drawings.
 */
@Composable
fun KnowledgeDiagram(visual: KnowledgeVisual) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    SoftCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxWidth().height(134.dp)
                    .semantics { contentDescription = visual.alt }
            ) {
                val w = size.width
                val h = size.height
                val count = visual.labels.size
                when (visual.kind) {
                    "sequence" -> {
                        val ys = h * 0.50f
                        val xs = (0 until count).map { w * (it + 0.5f) / count }
                        for (i in 0 until count - 1) {
                            drawLine(outline, Offset(xs[i] + w * .07f, ys),
                                Offset(xs[i + 1] - w * .07f, ys), strokeWidth = 5f,
                                cap = StrokeCap.Round)
                            drawLine(primary, Offset(xs[i + 1] - w * .09f, ys - 9f),
                                Offset(xs[i + 1] - w * .07f, ys), strokeWidth = 4f)
                            drawLine(primary, Offset(xs[i + 1] - w * .09f, ys + 9f),
                                Offset(xs[i + 1] - w * .07f, ys), strokeWidth = 4f)
                        }
                        for ((i, x) in xs.withIndex()) {
                            drawCircle(surface, radius = h * .25f, center = Offset(x, ys))
                            drawCircle(primary, radius = h * .20f, center = Offset(x, ys),
                                style = Stroke(width = 5f))
                            drawCircle(if (i == count - 1) secondary else primary,
                                radius = h * .055f, center = Offset(x, ys))
                        }
                    }
                    "checklist" -> {
                        val left = w * .13f
                        val top = h * .08f
                        drawRoundRect(surface, topLeft = Offset(left, top),
                            size = Size(w * .74f, h * .85f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f))
                        for (i in 0 until count) {
                            val y = top + h * (.21f + i * .22f)
                            val x = left + w * .075f
                            drawCircle(primary, radius = 8f, center = Offset(x, y))
                            drawLine(outline, Offset(x + w * .065f, y),
                                Offset(left + w * .63f, y), strokeWidth = 6f,
                                cap = StrokeCap.Round)
                        }
                    }
                    "shelves" -> {
                        // Each labelled storage zone corresponds to one column.
                        for (i in 0 until count) {
                            val x = w * (i + .5f) / count
                            val block = w * .55f / count
                            drawLine(outline,
                                Offset(x - w * .37f / count, h * .77f),
                                Offset(x + w * .37f / count, h * .77f),
                                strokeWidth = 7f, cap = StrokeCap.Round)
                            drawRoundRect(primary,
                                topLeft = Offset(x - block * .5f, h * .31f),
                                size = Size(block, h * .42f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f, 7f))
                            drawRoundRect(secondary,
                                topLeft = Offset(x - block * .32f, h * .22f),
                                size = Size(block * .64f, h * .09f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f))
                        }
                    }
                    "route" -> {
                        val xs = (0 until count).map { w * (it + .5f) / count }
                        val ys = (0 until count).map { h * (if (it % 2 == 0) .36f else .63f) }
                        for (i in 0 until count - 1) {
                            drawLine(primary, Offset(xs[i], ys[i]),
                                Offset(xs[i + 1], ys[i + 1]), strokeWidth = 7f,
                                cap = StrokeCap.Round)
                        }
                        for (i in xs.indices) {
                            drawCircle(surface, radius = 19f, center = Offset(xs[i], ys[i]))
                            drawCircle(secondary, radius = 12f, center = Offset(xs[i], ys[i]))
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()) {
                visual.labels.forEach { label ->
                    Text(label, modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Text(visual.caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Original offline schematic · Editorial draft",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}
