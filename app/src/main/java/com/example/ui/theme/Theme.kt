package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldPrimaryContainerDark,
    onPrimaryContainer = EmeraldOnPrimaryContainerDark,
    secondary = BioCyanSecondaryDark,
    onSecondary = BioCyanOnSecondaryDark,
    secondaryContainer = BioCyanSecondaryContainerDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    background = BackgroundDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = CriticalRed,
    errorContainer = CriticalRedContainer,
    onErrorContainer = CriticalRedOnContainer,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = BioCyanSecondary,
    onSecondary = BioCyanOnSecondary,
    secondaryContainer = BioCyanSecondaryContainer,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    background = BackgroundLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    tertiary = AlertAmber,
    onTertiary = AlertAmberOnTertiary,
    tertiaryContainer = AlertAmberContainer,
    onTertiaryContainer = AlertAmberOnContainer,
    error = CriticalRed,
    errorContainer = CriticalRedContainer,
    onErrorContainer = CriticalRedOnContainer,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set default to our custom branded palette
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
