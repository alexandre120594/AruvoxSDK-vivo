package com.example.testidentificasdk.dialer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.testidentificasdk.callhistory.CallHistoryTestActivity
import com.example.testidentificasdk.contact.ContactsTestActivity
import com.example.testidentificasdk.spam.SpamSdkTestActivity
import com.example.testidentificasdk.theme.VivoUiConfig
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.dialer.callbacks.DialerActions
import com.resolveja.aruvox.sdk.dialer.ui.AruvoxDialerScreen
import com.resolveja.aruvox.sdk.outgoing.domain.OutgoingCallState
import com.resolveja.aruvox.sdk.outgoing.ui.AruvoxOutgoingActiveScreen
import com.resolveja.aruvox.sdk.outgoing.ui.AruvoxOutgoingScreen
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme
import kotlinx.coroutines.launch
import com.resolveja.aruvox.sdk.R

class DialerTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val outgoingManager = AruvoxSDK.outgoing
                ?: error("OutgoingManager not initialized")

            val outgoingState by outgoingManager.uiState.collectAsStateWithLifecycle()
            val activeUiState by outgoingManager.activeUiState.collectAsStateWithLifecycle()
            val callDuration by outgoingManager.callDurationSeconds.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val pendingNumber = remember { mutableStateOf<String?>(null) }

            val callPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) {
                        pendingNumber.value?.let { number ->
                            lifecycleScope.launch {
                                outgoingManager.startCall(destinyPhone = number)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@DialerTestActivity,
                            "Permissão de chamada negada",
                            Toast.LENGTH_SHORT
                        ).show()
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
            val showBottomBar = when (outgoingState) {
                OutgoingCallState.Idle,
                OutgoingCallState.Ended -> true
                else -> false
            }

            AruvoxSdkTheme(config = VivoUiConfig) {
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                modifier = Modifier.height(64.dp),
                                tonalElevation = 2.dp,
                                containerColor = VivoUiConfig.colors.surface
                            ) {

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        context.startActivity(
                                            Intent(context, CallHistoryTestActivity::class.java)
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.history),
                                            contentDescription = "Histórico",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Histórico",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = VivoUiConfig.colors.primary,
                                        selectedTextColor = VivoUiConfig.colors.primary,
                                        unselectedIconColor = VivoUiConfig.colors.onPrimary,
                                        unselectedTextColor = VivoUiConfig.colors.onPrimary,
                                        indicatorColor = VivoUiConfig.colors.primary.copy(alpha = 0.12f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        context.startActivity(
                                            Intent(context, SpamSdkTestActivity::class.java)
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.block),
                                            contentDescription = "Spam",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Spam",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = VivoUiConfig.colors.primary,
                                        selectedTextColor = VivoUiConfig.colors.primary,
                                        unselectedIconColor = VivoUiConfig.colors.onPrimary,
                                        unselectedTextColor = VivoUiConfig.colors.onPrimary,
                                        indicatorColor = VivoUiConfig.colors.primary.copy(alpha = 0.12f)
                                    )
                                )
                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        context.startActivity(
                                            Intent(context, ContactsTestActivity::class.java)
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.person),
                                            contentDescription = "Spam",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Contato",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = VivoUiConfig.colors.primary,
                                        selectedTextColor = VivoUiConfig.colors.primary,
                                        unselectedIconColor = VivoUiConfig.colors.onPrimary,
                                        unselectedTextColor = VivoUiConfig.colors.onPrimary,
                                        indicatorColor = VivoUiConfig.colors.primary.copy(alpha = 0.12f)
                                    )
                                )

                            }
                        }
                    }
                ) { padding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {

                        // Dialer Screen
                        AruvoxDialerScreen(
                            actions = actions
                        )

                        // Outgoing states
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

                            is OutgoingCallState.Active ->{
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
}