package com.example.testidentificasdk

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.resolveja.aruvox.sdk.AruvoxSDK
import com.resolveja.aruvox.sdk.Environment
import com.resolveja.aruvox.sdk.SDKConfig
import java.util.UUID

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.util.Log

class TestApp : Application() {

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        val deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val correlationId = UUID.randomUUID().toString()

        AruvoxSDK.initialize(
            config = SDKConfig(
                baseUrl = "https://api-origem-verificada.sandbox.ssi.aws.cleartech.com.br",
                ssBaseUrl = "https://api-k8s-stirshift.sandbox.ssi.aws.cleartech.com.br" ,
                apiKey = "jWEvuRd9dlcQrFe3msEN6Cqki6221rv39ri9fp8a",
                deviceId = deviceId,
                correlationId = correlationId,
                environment = Environment.SANDBOX,
                enableLogs = true
            ),
            context = this
        )
    }
}


