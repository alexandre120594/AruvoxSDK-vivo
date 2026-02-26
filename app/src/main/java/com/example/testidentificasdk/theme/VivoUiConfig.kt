package com.example.testidentificasdk.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxShapes
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxTypography
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxUiConfig

val VivoUiConfig = AruvoxUiConfig(

    colors = AruvoxColors(
        primary = Color(0xFF6F00FF),
        onPrimary = Color.White,

        background = Color(0xFF1A0033),
        surface = Color(0xFF2E005F),
        surfaceVariant = Color(0xFF4B0082),

        textPrimary = Color.White,
        textSecondary = Color(0xFFD1C4E9),
        textMuted = Color(0xFFB39DDB),

        inputBackground = Color(0xFF3B0078),
        border = Color(0xFF7C4DFF),
        divider = Color(0xFF5E35B1),

        error = Color(0xFFFF3B30)
    ),

    shapes = AruvoxShapes(
        small = 12.dp,
        medium = 18.dp,
        large = 24.dp
    ),

    typography = AruvoxTypography(

        displayLarge = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        ),

        headlineMedium = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),

        titleMedium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),

        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        ),

        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),

        bodySmall = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    )
)