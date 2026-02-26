package com.example.testidentificasdk.spam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.testidentificasdk.theme.VivoUiConfig
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.core.ui.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.AruvoxTheme
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import com.resolveja.aruvox.sdk.spam.domain.provider.SystemBlockedNumbersProvider
import com.resolveja.aruvox.sdk.spam.internal.android.AndroidBlockedNumbersProvider
import com.resolveja.aruvox.sdk.spam.ui.AruvoxSpamScreen
import kotlinx.coroutines.launch


class SpamSdkTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                val result = AruvoxSDK.spam.getCallTypes()
                val result2 = AruvoxSDK.spam.getNumberTypes()
                Log.d("TEST", "Number types: $result")
            } catch (e: Exception) {
                Log.e("TEST", "Error loading number types", e)
            }
        }
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val loggedPhone = prefs.getString("logged_phone", null)
        setContent {
            val context = LocalContext.current

            AruvoxSdkTheme(config = VivoUiConfig)  {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    AruvoxSpamScreen(
                        spamManager = AruvoxSDK.spam,
                        reporterNumber = loggedPhone.toString(),
                        systemBlockedNumbersProvider =
                            AndroidBlockedNumbersProvider(context = context)
                    )
                }
            }

        }
    }
}


val AruvoxGenericTheme = AruvoxTheme(
    colors = AruvoxColors(
        background = Color(0xFF1E1E1E),   // dark blue / slate
        primary = Color(0xFF2563EB),      // blue
        textPrimary = Color.White,
        secondary = Color(0, 0, 0, 51),
        success = Color(0xFF22C55E),      // green
        error = Color(0xFFEF4444)         // red
    )
)
