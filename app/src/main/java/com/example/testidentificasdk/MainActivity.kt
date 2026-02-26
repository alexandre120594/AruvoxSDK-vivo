package com.example.testidentificasdk

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.testidentificasdk.components.SdkTestActivity
import com.example.testidentificasdk.dialer.DialerTestActivity
import com.example.testidentificasdk.spam.SpamSdkTestActivity
import com.resolveja.aruvox.sdk.AruvoxSDK
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestDialerRole()
    }

    private fun checkAndRequestDialerRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager

            if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent =
                    roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                startActivityForResult(intent, 1001)
            } else {
                launchApp()
            }
        } else {
            val telecomManager =
                getSystemService(Context.TELECOM_SERVICE) as TelecomManager

            if (telecomManager.defaultDialerPackage != packageName) {
                val intent =
                    Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                        putExtra(
                            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                            packageName
                        )
                    }
                startActivity(intent)
            } else {
                launchApp()
            }
        }
    }

    private fun launchApp() {
        lifecycleScope.launch {

            val isAuthenticated =
                try {
                    AruvoxSDK.auth.isAuthenticated()
                } catch (e: Exception) {
                    false
                }

            val nextActivity =
                if (isAuthenticated) {
                    DialerTestActivity::class.java
                } else {
                    SdkTestActivity::class.java
                }

            startActivity(Intent(this@MainActivity, nextActivity))
            finish()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            launchApp()
        }
    }
}
