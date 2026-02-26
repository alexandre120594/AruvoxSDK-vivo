//package com.example.testidentificasdk.dialer
//
//import com.resolveja.aruvox.sdk.dialer.callbacks.DialerDataProvider
//import com.resolveja.aruvox.sdk.dialer.domain.DialerSim
//import com.resolveja.aruvox.sdk.dialer.result.DialerCallInfoResult
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//
//class FakeDialerProvider : DialerDataProvider {
//
//    private val _callInfo =
//        MutableStateFlow<DialerCallInfoResult>(DialerCallInfoResult.Idle)
//
//    private val _sims =
//        MutableStateFlow(
//            listOf(
//                DialerSim(
//                    carrierName = "Fake Carrier",
//                    subscriptionId = 1,
//                    slotIndex = 0
//                )
//            )
//        )
//
//    override val callInfoResult: StateFlow<DialerCallInfoResult> = _callInfo
//    override val availableSims: StateFlow<List<DialerSim>> = _sims
//
//    override fun requestCallInfo(processedNumber: String) {
//        _callInfo.value = DialerCallInfoResult.Success(
//            name = "Número Teste",
//            logoBase64 = ""
//        )
//    }
//
//    override fun getContactName(phone: String): String? {
//        return "Contato Fake"
//    }
//}
