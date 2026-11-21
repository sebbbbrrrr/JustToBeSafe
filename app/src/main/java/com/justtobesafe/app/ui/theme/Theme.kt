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

/** Calm Utility: these are prototype tokens, not final trademark or brand clearance. */
val DeepInk = Color(0xFF142C3A)
val OceanTeal = Color(0xFF08776D)
val SoftTeal = Color(0xFFD9F0E9)
val Mist = Color(0xFFF3F8FA)
val MutedInk = Color(0xFF506675)
val OutlineSoft = Color(0xFFDCE7EA)
val Amber = Color(0xFFB47626)
val OceanBlue = Color(0xFF3F7795)
val DuskBlue = Color(0xFFB8DCE8)

private val CalmLight = lightColorScheme(
    primary = OceanTeal,
    onPrimary = Color.White,
    primaryContainer = SoftTeal,
    onPrimaryContainer = Color(0xFF075148),
    secondary = Color(0xFF446F82),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDEF0F5),
    onSecondaryContainer = DeepInk,
    tertiary = Amber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEACC),
    onTertiaryContainer = Color(0xFF664108),
    background = Mist,
    onBackground = DeepInk,
    surface = Color.White,
    onSurface = DeepInk,
    surfaceVariant = Color(0xFFEAF2F5),
    onSurfaceVariant = MutedInk,
    outline = Color(0xFF758B96),
    outlineVariant = OutlineSoft,
    surfaceContainerLow = Color(0xFFF9FCFD),
    surfaceContainer = Color(0xFFF0F6F8),
    surfaceContainerHigh = Color(0xFFE8F1F4),
    error = Color(0xFFB13841),
    onError = Color.White
)

private val CalmDark = darkColorScheme(
    primary = Color(0xFF82DBCF),
    onPrimary = Color(0xFF003B34),
    primaryContainer = Color(0xFF17534D),
    onPrimaryContainer = Color(0xFFD3F8EF),
    secondary = Color(0xFFA7CEDD),
    onSecondary = Color(0xFF0D3646),
    secondaryContainer = Color(0xFF2E4955),
    onSecondaryContainer = Color(0xFFD7EEF5),
    tertiary = Color(0xFFF0C68A),
    onTertiary = Color(0xFF4E3208),
    tertiaryContainer = Color(0xFF60451C),
    onTertiaryContainer = Color(0xFFFFE6BE),
    background = Color(0xFF111D25),
    onBackground = Color(0xFFEAF1F3),
    surface = Color(0xFF1C2B34),
    onSurface = Color(0xFFEAF1F3),
    surfaceVariant = Color(0xFF293C47),
    onSurfaceVariant = Color(0xFFB7CCD5),
    outline = Color(0xFF94ABB5),
    outlineVariant = Color(0xFF405661),
    surfaceContainerLow = Color(0xFF16252D),
    surfaceContainer = Color(0xFF21313B),
    surfaceContainerHigh = Color(0xFF293C47),
    error = Color(0xFFFFB4B6),
    onError = Color(0xFF620010)
)

private val CalmTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 29.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 25.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun CalmUtilityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) CalmDark else CalmLight,
        typography = CalmTypography,
        content = content
    )
}
