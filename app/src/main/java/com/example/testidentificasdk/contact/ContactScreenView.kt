package com.example.testidentificasdk.contact

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.resolveja.aruvox.contato.ui.AruvoxContactDetailsScreen
import com.resolveja.aruvox.contato.ui.AruvoxContactsScreen
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.contato.result.ContactDetailsResult
import com.resolveja.aruvox.sdk.contato.result.ContatoResult
import com.resolveja.aruvox.sdk.core.ui.AruvoxColors
import com.resolveja.aruvox.sdk.core.ui.AruvoxTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.resolveja.aruvox.sdk.core.ui.theme.AruvoxSdkTheme


class ContactsTestActivity : ComponentActivity() {
    private var selectedContactId: String? = null
    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                showContacts()
            } else {
                Log.e("SDK_TEST", "READ_CONTACTS permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun showContacts() {
        setContent {

            var selectedContactId by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf<String?>(null)
            }

                Surface(modifier = Modifier.fillMaxSize()) {

                    if (selectedContactId == null) {
                        AruvoxSdkTheme {
                            AruvoxContactsScreen(
                                contactsManager = AruvoxSDK.contacts,
                                onResult = ::handleResult,
                                onOpenDetails = { contactId ->
                                    selectedContactId = contactId
                                }
                            )
                        }
                    } else {
                        // 🔹 CONTACT DETAILS
                        AruvoxSdkTheme {
                            AruvoxContactDetailsScreen(
                                contactsManager = AruvoxSDK.contacts,
                                contactId = selectedContactId!!,
                                onResult = { result ->
                                    when (result) {
                                        is ContactDetailsResult.Back -> {
                                            selectedContactId = null
                                        }
                                        else -> {
                                            handleResultFromDetails(result)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    private fun handleResult(result: ContatoResult) {
        when (result) {
            is ContatoResult.Call -> {
                Log.d("SDK_TEST", "Call phone: ${result.phone}")
                // navigate to call screen
            }

            is ContatoResult.EditContact -> {
                Log.d("SDK_TEST", "Edit contact: ${result.contact.id}")
                // open edit flow
            }

            ContatoResult.AddContact -> {
                Log.d("SDK_TEST", "Add new contact")
                // open add flow
            }

        }
    }

private fun handleResultFromDetails(
    result: com.resolveja.aruvox.sdk.contato.result.ContactDetailsResult
) {
    when (result) {
        is ContactDetailsResult.Call -> {
            Log.d("SDK_TEST", "Call from details: ${result.phone}")
        }

        is ContactDetailsResult.Edit -> {
            Log.d("SDK_TEST", "Edit from details: ${result.contactId}")
        }

        is ContactDetailsResult.Delete -> {
            Log.d("SDK_TEST", "Delete contact: ${result.contactId}")
        }

        is ContactDetailsResult.Block -> {
            Log.d("SDK_TEST", "Block phone: ${result.phone}")
        }

        is ContactDetailsResult.Unblock -> {
            Log.d("SDK_TEST", "Unblock phone: ${result.phone}")
        }

        ContactDetailsResult.Back -> {
            // already handled
        }

        is ContactDetailsResult.SendSms -> TODO()
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
