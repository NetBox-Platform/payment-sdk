package ir.net_box.paymentclient.payment

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import ir.net_box.payment.IPaymentService

/**
 * Standard [ServiceConnection] implementation for binding to the [IPaymentService] AIDL interface.
 *
 * @param onServiceConnected Lambda invoked when the service is successfully bound.
 * @param onServiceDisconnected Lambda invoked when the service is unexpectedly disconnected.
 */
internal open class PaymentServiceConnection(
    private val onServiceConnected: () -> Unit,
    private val onServiceDisconnected: () -> Unit
) : ServiceConnection {
    /** The AIDL interface for interacting with the payment service. */
    var iPaymentService: IPaymentService? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected" + name?.packageName)
        IPaymentService.Stub.asInterface(service)?.also {
            iPaymentService = it
            onServiceConnected()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected")
        cleanup()
        onServiceDisconnected()
    }

    private fun cleanup() {
        iPaymentService = null
    }

    companion object {
        private const val TAG = "PaymentConnection"
    }
}