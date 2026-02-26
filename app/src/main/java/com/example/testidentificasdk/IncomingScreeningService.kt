package com.example.testidentificasdk

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.resolveja.aruvox.sdk.AruvoxSDK
import java.util.UUID

class IncomingScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {

        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(
                callDetails,
                CallResponse.Builder().build()
            )
            return
        }

        val originPhone =
            callDetails.handle?.schemeSpecificPart ?: run {
                respondToCall(
                    callDetails,
                    CallResponse.Builder().build()
                )
                return
            }

        val intent = Intent(this, IncomingTestActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra("originPhone", originPhone)
        }

        startActivity(intent)

        respondToCall(
            callDetails,
            CallResponse.Builder().build()
        )
    }
}