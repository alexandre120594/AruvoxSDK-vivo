package com.example.testidentificasdk

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.telephony.TelephonyManager
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.core.utils.formatToBrazilE164
import kotlinx.coroutines.*


class AppInCallService : InCallService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)

        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val originPhone = call.details.handle?.schemeSpecificPart.orEmpty()
        val carrierName = telephonyManager.networkOperatorName
            .takeIf { it.isNotBlank() } ?: "Unknown"

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val loggedPhone = prefs.getString("logged_phone", null)


        when (call.details.callDirection) {

            Call.Details.DIRECTION_INCOMING -> {
                AruvoxSDK.incoming?.bindIncomingCall(
                    call = call,
                    originPhone = formatToBrazilE164(originPhone),
                    destinyPhone = formatToBrazilE164(loggedPhone.toString()),
                    carrierName = carrierName,
                )

                val intent = Intent(this, IncomingTestActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    putExtra("originPhone", originPhone)
                }

                startActivity(intent)
            }

            Call.Details.DIRECTION_OUTGOING -> {
                serviceScope.launch {
                    AruvoxSDK.outgoing?.bindOutgoingCall(
                        call = call,
                        formatToBrazilE164(loggedPhone.toString())
                    )
                }
            }
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        // Nada aqui: o SDK limpa quando recebe DISCONNECTED
    }
}
