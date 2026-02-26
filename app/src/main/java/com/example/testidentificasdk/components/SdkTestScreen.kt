package com.example.testidentificasdk.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.resolveja.aruvox.sdk.AruvoxSDK
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import com.resolveja.aruvox.sdk.auth.result.OtpSession
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.testidentificasdk.dialer.DialerTestActivity
import com.resolveja.aruvox.sdk.dialer.callbacks.DialerActions
import androidx.core.content.edit
import com.resolveja.aruvox.sdk.auth.result.AuthResult


class SdkTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface {
                    SdkTestScreen()
                }
            }
        }
    }
}
@Composable
fun SdkTestScreen() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSession: String? by remember { mutableStateOf<String?>(null) }

    var status by remember { mutableStateOf("Pronto") }
    var loading by remember { mutableStateOf(false) }

    val vivoPurple = Color(0xFF6F00FF)
    val vivoDarkPurple = Color(0xFF4B0082)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(vivoPurple, vivoDarkPurple)
                )
            )
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Acesso",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Entre com seu número para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone (+55...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                status = "Enviando OTP..."

                                try {
                                    val result = AruvoxSDK.auth.sendOtp(phone)
                                    if (result.isSuccess) {
                                        otpSession = result.getOrNull()?.toString()
                                        status = "OTP enviado"
                                    } else {
                                        status = "Erro: ${result.exceptionOrNull()?.message}"
                                    }
                                } catch (e: Exception) {
                                    status = "Erro: ${e.message}"
                                }

                                loading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Receber código")
                        }
                    }

                    if (otpSession != null) {

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            label = { Text("Código OTP") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    loading = true
                                    status = "Validando..."

                                    try {
                                        val result = AruvoxSDK.auth.verifyOtp(
                                            phone = phone,
                                            code = otp,
                                            session = otpSession!!
                                        )

                                        when (result) {
                                            is AuthResult.Success -> {

                                                context.getSharedPreferences("auth", Activity.MODE_PRIVATE)
                                                    .edit {
                                                        putString("logged_phone", phone)
                                                    }

                                                context.startActivity(
                                                    Intent(context, DialerTestActivity::class.java)
                                                )

                                                (context as Activity).finish()
                                                status = "Autenticado com sucesso"
                                            }

                                            is AuthResult.Failure -> {
                                                status = "Erro: ${result.message}"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        status = "Erro: ${e.message}"
                                    }

                                    loading = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !loading
                        ) {
                            Text("Entrar")
                        }
                    }
                }
            }

            Text(
                text = status,
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
