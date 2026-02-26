package com.example.testidentificasdk

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.core.ui.AruvoxTheme
import com.resolveja.aruvox.sdk.incoming.domain.IncomingCallState
import com.resolveja.aruvox.sdk.incoming.ui.AruvoxIncomingScreen
import androidx.compose.ui.graphics.Color
import com.example.testidentificasdk.theme.VivoUiConfig
import com.resolveja.aruvox.sdk.core.ui.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import com.resolveja.aruvox.sdk.incoming.ui.AruvoxIncomingActiveScreen

class IncomingTestActivity : ComponentActivity() {

    // Registro para capturar o resultado da solicitação de Role
    private val requestRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "App definido como Filtro de Chamadas!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCallScreeningRole()

        setContent {
            val incoming = AruvoxSDK.incoming ?: return@setContent Text("SDK não inicializado")
            val state by incoming.state.collectAsStateWithLifecycle()
            val activeUiState by incoming.activeUiState.collectAsStateWithLifecycle()
            val callDuration by incoming.callDurationSeconds.collectAsStateWithLifecycle()

            when (state) {
                is IncomingCallState.Ringing -> {
                    val ui = (state as IncomingCallState.Ringing).uiModel
                    AruvoxSdkTheme(config = VivoUiConfig) {
                        AruvoxIncomingScreen(
                            uiModel = ui,
                            onAnswer = { callId ->
                                AruvoxSDK.incoming?.answer(callId)
                            },
                            onReject = { callId ->
                                AruvoxSDK.incoming?.reject(callId)
                            }
                        )
                    }

                }
                is IncomingCallState.Active -> {
                    val ui = (state as IncomingCallState.Active).uiModel
                    AruvoxSdkTheme(config = VivoUiConfig) {
                        AruvoxIncomingActiveScreen(
                            uiModel = ui,
                            activeUiState = activeUiState,
                            callDurationSeconds = callDuration,
                            onEndCall = { callId ->
                                incoming.reject(callId)
                            },
                            onToggleHold = { callId ->
                                incoming.toggleHold(callId)
                            },
                            onToggleMute = { callId ->
                                incoming.toggleHold(callId)
                            },
                            onToggleSpeaker = { callId ->
                                incoming.toggleHold(callId)
                            }
                        )
                    }
                }
                is IncomingCallState.Ended -> {
                   finish()
                }
                IncomingCallState.Idle -> Text("Aguardando chamada (Role Ativo)")
            }
        }
    }

    private fun requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                requestRoleLauncher.launch(intent)
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

