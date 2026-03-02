package com.example.testidentificasdk.callhistory

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.testidentificasdk.theme.VivoUiConfig
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.callhistory.ui.AruvoxCallHistoryScreen
import com.resolveja.aruvox.sdk.callhistory.internal.ui.CallHistoryUIConfig
import com.resolveja.aruvox.sdk.spam.ui.AruvoxSpamBlockScreen
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import com.resolveja.aruvox.sdk.core.utils.normalizeNumber
import com.resolveja.aruvox.sdk.spam.internal.android.AndroidBlockedNumbersProvider
import kotlinx.coroutines.launch

class CallHistoryTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AruvoxSDK.callHistory
                .observeCustomHistory()
                .collect { list ->
                    Log.d("DB_DEBUG", "DATA = $list")
                }
        }
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val loggedPhone = prefs.getString("logged_phone", null)


        setContent {

            AruvoxSdkTheme(config = VivoUiConfig) {

                var blockNumber by remember { mutableStateOf<String?>(null) }

                if (blockNumber == null) {

                    AruvoxCallHistoryScreen(
                        manager = AruvoxSDK.callHistory,
                        config = CallHistoryUIConfig(
                            limit = 20,
                            onBlockClick = { number ->
                                blockNumber = number
                            }
                        )
                    )

                } else {

                    AruvoxSpamBlockScreen(
                        spamManager = AruvoxSDK.spam,
                        blockedNumber = normalizeNumber(blockNumber),
                        reporterNumber = loggedPhone.orEmpty(),
                        onSuccess = { blockNumber = null },
                        systemBlockedNumbersProvider =
                            AndroidBlockedNumbersProvider(context = this)
                    )
                }
            }
        }
    }
}