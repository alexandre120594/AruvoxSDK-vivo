package com.example.testidentificasdk.dialer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testidentificasdk.AruvoxGenericTheme

import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.core.ui.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.AruvoxTheme
import com.resolveja.aruvox.sdk.dialer.callbacks.DialerActions
import com.resolveja.aruvox.sdk.dialer.ui.AruvoxDialerScreen
import com.resolveja.aruvox.sdk.outgoing.domain.OutgoingCallState
import com.resolveja.aruvox.sdk.outgoing.domain.OutgoingAction
import com.resolveja.aruvox.sdk.outgoing.ui.AruvoxOutgoingActiveScreen
import com.resolveja.aruvox.sdk.outgoing.ui.AruvoxOutgoingScreen
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import com.resolveja.aruvox.sdk.incoming.domain.IncomingCallState
import com.resolveja.aruvox.sdk.outgoing.OutgoingManager
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.testidentificasdk.callhistory.CallHistoryTestActivity
import com.example.testidentificasdk.spam.SpamSdkTestActivity
import com.example.testidentificasdk.theme.VivoUiConfig

import kotlinx.coroutines.*

class DialerTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val outgoingManager = AruvoxSDK.outgoing
                ?: error("OutgoingManager not initialized")

            val outgoingState by outgoingManager
                .uiState
                .collectAsStateWithLifecycle()

            val activeUiState by outgoingManager.activeUiState.collectAsStateWithLifecycle()
            val callDuration by outgoingManager.callDurationSeconds.collectAsStateWithLifecycle()
            val pendingNumber = remember { mutableStateOf<String?>(null) }
            LaunchedEffect(outgoingState) {
                Log.d("OUTGOING_DEBUG", "State = $outgoingState")
            }
           val context = LocalContext.current
            val callPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) {
                        pendingNumber.value?.let { number ->
                            lifecycleScope.launch {
                                outgoingManager?.startCall(
                                    destinyPhone = number,
                                )
                            }
                        }
                    } else {
                        Toast
                            .makeText(
                                this@DialerTestActivity,
                                "Permissão de chamada negada",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }

            val actions = remember {
                object : DialerActions {

                    override fun onCallRequested(number: String) {
                        if (ContextCompat.checkSelfPermission(
                                this@DialerTestActivity,
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            lifecycleScope.launch {
                                context.getSharedPreferences("call", MODE_PRIVATE)
                                    .edit {
                                        putString("call_phone", number)
                                    }

                                outgoingManager.startCall(
                                    destinyPhone = number
                                )
                                pendingNumber.value = null
                            }
                        } else {
                            pendingNumber.value = number
                            callPermissionLauncher.launch(
                                Manifest.permission.CALL_PHONE
                            )
                        }
                    }

                    override fun onSimSelected(subscriptionId: Int) = Unit
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VivoUiConfig.colors.background)
                    .systemBarsPadding()
                    .padding(top = 20.dp)
            ) {
                AruvoxSdkTheme(config = VivoUiConfig) {
                    AruvoxDialerScreen(
                        actions = actions
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            startActivity(
                                Intent(
                                    this@DialerTestActivity,
                                    CallHistoryTestActivity::class.java
                                )
                            )
                        }
                    ) {
                        Text("Histórico")
                    }

                    Button(
                        onClick = {
                            startActivity(
                                Intent(
                                    this@DialerTestActivity,
                                    SpamSdkTestActivity::class.java
                                )
                            )
                        }
                    ) {
                        Text("Spam")
                    }
                }




                AruvoxSdkTheme(config = VivoUiConfig) {

                        when (val state = outgoingState) {

                            OutgoingCallState.Idle -> Unit

                            OutgoingCallState.Ended -> Unit

                            is OutgoingCallState.Dialing -> {
                                AruvoxOutgoingScreen(
                                    uiModel = state.uiModel,
                                    onEndCall = { outgoingManager.endCall(it) },
                                    onHold = { outgoingManager.toggleHold(it) },
                                    onResume = { outgoingManager.toggleHold(it) },
                                    onMute = { outgoingManager.toggleMute(it) },
                                    onSpeaker = { outgoingManager.toggleSpeaker(it) },
                                    onSendDtmf = { id, digit ->
                                        outgoingManager.sendDtmf(id, digit)
                                    }
                                )
                            }

                            is OutgoingCallState.Connecting -> {
                                AruvoxOutgoingScreen(
                                    uiModel = state.uiModel,
                                    onEndCall = { outgoingManager.endCall(it) },
                                    onHold = { outgoingManager.toggleHold(it) },
                                    onResume = { outgoingManager.toggleHold(it) },
                                    onMute = { outgoingManager.toggleMute(it) },
                                    onSpeaker = { outgoingManager.toggleSpeaker(it) },
                                    onSendDtmf = { id, digit ->
                                        outgoingManager.sendDtmf(id, digit)
                                    }
                                )
                            }

                            is OutgoingCallState.Active -> {
                                AruvoxOutgoingActiveScreen(
                                    uiModel = state.uiModel,
                                    activeState = activeUiState,
                                    callDurationSeconds = callDuration,
                                    onEndCall = { outgoingManager.endCall(it) },
                                    onToggleHold = { outgoingManager.toggleHold(it) },
                                    onSendDtmf = { id, digit ->
                                        outgoingManager.sendDtmf(id, digit)
                                    },
                                    onToggleSpeaker = { outgoingManager.toggleSpeaker(it) }
                                )
                            }

                            is OutgoingCallState.Hold -> {
                                AruvoxOutgoingActiveScreen(
                                    uiModel = state.uiModel,
                                    activeState = activeUiState,
                                    callDurationSeconds = callDuration,
                                    onEndCall = { outgoingManager.endCall(it) },
                                    onToggleHold = { outgoingManager.toggleHold(it) },
                                    onSendDtmf = { id, digit ->
                                        outgoingManager.sendDtmf(id, digit)
                                    },
                                    onToggleSpeaker = { outgoingManager.toggleSpeaker(it) }
                                )
                            }

                            else -> Unit
                        }
                    }
            }
        }
    }
}

