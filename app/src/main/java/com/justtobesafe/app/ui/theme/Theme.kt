package com.justtobesafe.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DeepInk = Color(0xFF152837)
val OceanTeal = Color(0xFF087C72)
val SoftTeal = Color(0xFFDAF1EB)
val Mist = Color(0xFFF4F8FA)
val MutedInk = Color(0xFF506776)
val OutlineSoft = Color(0xFFDCE7EB)
val Amber = Color(0xFFB77820)

private val LightColors = lightColorScheme(
    primary = OceanTeal, onPrimary = Color.White,
    primaryContainer = SoftTeal, onPrimaryContainer = Color(0xFF074B46),
    secondary = Color(0xFF476B7C), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0EEF5), onSecondaryContainer = DeepInk,
    tertiary = Amber, onTertiary = Color.White,
    background = Mist, onBackground = DeepInk,
    surface = Color.White, onSurface = DeepInk,
    surfaceVariant = Color(0xFFEDF4F6), onSurfaceVariant = MutedInk,
    outline = Color(0xFF76909D), outlineVariant = OutlineSoft,
    error = Color(0xFFB2333A), onError = Color.White
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF80DCCF), onPrimary = Color(0xFF003E37),
    primaryContainer = Color(0xFF0B534D), onPrimaryContainer = Color(0xFFD0F8EE),
    secondary = Color(0xFFABCEDB), onSecondary = Color(0xFF163744),
    secondaryContainer = Color(0xFF2A4553), onSecondaryContainer = Color(0xFFD0E7EF),
    tertiary = Color(0xFFFFCC82), onTertiary = Color(0xFF573300),
    background = Color(0xFF101B22), onBackground = Color(0xFFE7F0F4),
    surface = Color(0xFF192A33), onSurface = Color(0xFFE7F0F4),
    surfaceVariant = Color(0xFF263A44), onSurfaceVariant = Color(0xFFB6CAD3),
    outline = Color(0xFF9AB0BB), outlineVariant = Color(0xFF425964),
    error = Color(0xFFFFB4AE), onError = Color(0xFF690006)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 29.sp, lineHeight = 35.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 25.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun CalmUtilityTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography, content = content)
}
