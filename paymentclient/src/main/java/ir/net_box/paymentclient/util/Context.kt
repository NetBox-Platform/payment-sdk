package ir.net_box.paymentclient.util

import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Attempts to start an activity using the provided [intent].
 *
 * @param intent The intent to start.
 * @param onSuccess Optional callback to execute if the activity starts successfully.
 */
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