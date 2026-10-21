package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Scientific Metallic Dark Theme (تم تیره علمی و متالیک با تناژ ابسیدین و تیتانیوم)
private val ScientificMetallicDarkColorScheme = darkColorScheme(
    primary = MetallicCyan,
    onPrimary = Color(0xFF031525),
    primaryContainer = DarkSurface2,
    onPrimaryContainer = TextPrimary,
    secondary = MetallicAmber,
    onSecondary = Color(0xFF1E1300),
    secondaryContainer = DarkSurface3,
    onSecondaryContainer = TextPrimary,
    tertiary = MetallicEmerald,
    onTertiary = Color(0xFF001F0E),
    background = DarkBackground,
    surface = DarkSurface1,
    surfaceVariant = DarkSurface2,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DarkSeparator,
    outlineVariant = MetallicBorder
)

// Eye-Care Warm Dark Theme (حالت مطالعه شبانه با فیلتر کاهش نور آبی جهت مطالعه طولانی‌مدت کنکور)
private val EyeCareWarmDarkColorScheme = darkColorScheme(
    primary = MetallicAmber,
    onPrimary = Color(0xFF1E1300),
    primaryContainer = EyeCareWarmSurface2,
    onPrimaryContainer = EyeCareWarmTextPrimary,
    secondary = MetallicCyan,
    onSecondary = Color(0xFF031525),
    secondaryContainer = DarkSurface3,
    onSecondaryContainer = EyeCareWarmTextPrimary,
    tertiary = MetallicEmerald,
    onTertiary = Color(0xFF001F0E),
    background = EyeCareWarmBackground,
    surface = EyeCareWarmSurface1,
    surfaceVariant = EyeCareWarmSurface2,
    onBackground = EyeCareWarmTextPrimary,
    onSurface = EyeCareWarmTextPrimary,
    onSurfaceVariant = EyeCareWarmTextSecondary,
    outline = DarkSeparator,
    outlineVariant = MetallicBorder
)

@Composable
fun MyApplicationTheme(
    isEyeCareMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isEyeCareMode) EyeCareWarmDarkColorScheme else ScientificMetallicDarkColorScheme
    val bg = if (isEyeCareMode) EyeCareWarmBackground else DarkBackground

    val navBg = if (isEyeCareMode) EyeCareWarmSurface1 else DarkSurface1

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = bg.toArgb()
                window.navigationBarColor = navBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
