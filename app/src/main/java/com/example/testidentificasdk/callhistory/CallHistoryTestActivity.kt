package com.example.testidentificasdk.callhistory

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.example.testidentificasdk.theme.VivoUiConfig
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.callhistory.CallHistoryManager
import com.resolveja.aruvox.sdk.callhistory.ui.AruvoxCallHistoryScreen
import com.resolveja.aruvox.sdk.callhistory.internal.ui.CallHistoryUIConfig
import com.resolveja.aruvox.sdk.core.ui.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.AruvoxTheme
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import kotlinx.coroutines.launch

class CallHistoryTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SDK MUST already be initialized in Application
        // AruvoxSDK.initialize(...)
        lifecycleScope.launch {
            AruvoxSDK.callHistory
                .observeCustomHistory()
                .collect { list ->
                    Log.d("DB_DEBUG", "DATA = $list")
                }
        }
        setContent {
            AruvoxSdkTheme(config = VivoUiConfig) {
                AruvoxCallHistoryScreen(
                    config = CallHistoryUIConfig(
                        limit = 20
                    ),
                    manager = AruvoxSDK.callHistory,
                )
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