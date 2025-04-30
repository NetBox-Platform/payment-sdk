package ir.net_box.paymentclient.connection

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.core.os.bundleOf
import ir.net_box.paymentclient.util.PACKAGE_NAME_ARG_KEY

internal class PaymentConnectionVerification(
    private val packageName: String,
    private val onServiceConnected: (Boolean) -> Unit,
    private val onServiceDisconnected: () -> Unit
) {

    /** Messenger for communicating with the service.  */
    var mService: Messenger? = null

    /**
     * Class for interacting with the main interface of the service.
     */
    val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = Messenger(service)
            Log.d("Verification", "onServiceConnected: PaymentConnectionVerification")
            onServiceConnected(verifyService())
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            onServiceDisconnected()
            Log.d("Verification", "onServiceDisconnected: PaymentConnectionVerification")
            mService = null
        }
    }

    private fun verifyService(): Boolean {
        val message: Message = Message.obtain(null, 1000, 0, 0)
        return try {
            Log.d("Verification", "verifyService: $message")
            message.data = bundleOf(
                PACKAGE_NAME_ARG_KEY to packageName
            )
            mService?.send(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clear() {
        mService = null
    }
}
