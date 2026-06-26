package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PremiumMint,
    secondary = ForestGreen,
    tertiary = MintAccent,
    background = Charcoal,
    surface = DarkGrey,
    onPrimary = Charcoal,
    onSecondary = Color.White,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
    primaryContainer = DeepGreen,
    onPrimaryContainer = MintAccent
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldGreen,
    secondary = ForestGreen,
    tertiary = DeepGreen,
    background = Color.White,
    surface = SoftWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkGrey,
    onSurface = DarkGrey,
    primaryContainer = LightSage,
    onPrimaryContainer = OnPrimaryContainerGreen
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
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
