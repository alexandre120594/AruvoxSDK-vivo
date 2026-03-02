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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestDialerRole()
    }

    private val contactsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = permissions[Manifest.permission.WRITE_CONTACTS] == true &&
                    permissions[Manifest.permission.READ_CONTACTS] == true

            if (granted) {
                launchApp()
            } else {
                // You can decide what to do here
                // For now, continue anyway
                checkContactsPermissionAndLaunch()
            }
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

    private fun checkContactsPermissionAndLaunch() {

        val readGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val writeGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (readGranted && writeGranted) {
            launchApp()
        } else {
            contactsPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
            )
        }
    }
}


