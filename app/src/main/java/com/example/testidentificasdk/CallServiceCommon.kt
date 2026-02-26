//package com.example.testidentificasdk
//
//import android.os.Build
//import android.telecom.Call
//import android.telecom.InCallService
//import androidx.annotation.RequiresApi
//import com.resolveja.aruvox.sdk.AruvoxSDK
//
//class CallServiceCommon : InCallService() {
//
//    override fun onCreate() {
//        super.onCreate()
//        println("📞 InCallService CREATED")
//    }
//
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun onCallAdded(call: Call) {
//        super.onCallAdded(call)
//
//        println("📞 onCallAdded | state=${call.details.state}")
//
//        if (call.details.callDirection != Call.Details.DIRECTION_INCOMING) return
//
//        val originPhone =
//            call.details.handle?.schemeSpecificPart ?: return
//
//        AruvoxSDK.incoming?.bindIncomingCall(
//            call = call,
//            originPhone = originPhone,
//            destinyPhone = "11988888888",
//            carrierName = "Teste",
//            accessToken = "fake-token"
//        )
//    }
//
//    override fun onCallRemoved(call: Call) {
//        super.onCallRemoved(call)
//        println("📞 onCallRemoved")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        println("📞 InCallService DESTROYED")
//    }
//}
