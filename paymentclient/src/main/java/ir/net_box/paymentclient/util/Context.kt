package ir.net_box.paymentclient.util

import android.content.Context
import android.content.Intent
import android.util.Log

fun Context.tryStartActivity(intent: Intent, onSuccess: (() -> Unit)? = null) {
    runCatching {
        startActivity(intent)
    }.onSuccess {
        onSuccess?.invoke()
    }.onFailure {
        it.printStackTrace()
        Log.d("PaymentConnection", "Netstore is outdated!")
    }
}