package ir.net_box.paymentclient.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ir.net_box.paymentclient.callback.ConnectionCallback
import ir.net_box.paymentclient.exception.ServiceNotInitializedException
import ir.net_box.paymentclient.payment.PaymentServiceConnection
import ir.net_box.paymentclient.util.PACKAGE_NAME_ARG_KEY
import ir.net_box.sso.NET_STORE_PACKAGE_NAME

class PaymentConnection(private val context: Context) : Payable {

    private var callback: ConnectionCallback? = null

    private var paymentServiceConnection: PaymentServiceConnection? = null

    private var verificationServiceConnection: PaymentConnectionVerification? = null

    fun startConnection(
        packageName: String,
        connectionCallback: (ConnectionCallback) -> Unit
    ): Connection {
        Log.d(TAG, "startConnection")
        paymentServiceConnection =
            PaymentServiceConnection(
                ::onServiceConnected,
                ::onServiceDisconnected
            )
        this.callback = ConnectionCallback(disconnect = ::disconnect).apply(connectionCallback)

        verificationServiceConnection = PaymentConnectionVerification({
            // onServiceConnected
            verified ->
            if (verified) {
                try {
                    // Second try to connect to netbox payment system
                    context.bindService(
                        Intent(PAYMENT_SERVICE_ACTION).apply {
                            `package` = NET_STORE_PACKAGE_NAME
                            setClassName(NET_STORE_PACKAGE_NAME, PAYMENT_SERVICE_CLASS_NAME)
                            putExtra(PACKAGE_NAME_ARG_KEY, packageName)
                        },
                        paymentServiceConnection!!, Context.BIND_AUTO_CREATE
                    )
                } catch (e: SecurityException) {
                    this.callback?.connectionFailed?.invoke(e)
                    e.printStackTrace()
                }
            } else {
                this.callback?.connectionFailed
                    ?.invoke(Throwable("Connection failed. please try again"))
            }
        }) {
            // onServiceDisconnected
            this.callback?.connectionFailed?.invoke(Throwable("Bad request!"))
        }

        // First we verify your app validation
        try {
            context.bindService(
                Intent(PAYMENT_SERVICE_ACTION).apply {
                    `package` = NET_STORE_PACKAGE_NAME
                    setClassName(NET_STORE_PACKAGE_NAME, PAYMENT_SERVICE_VERIFICATION_CLASS_NAME)
                    putExtra(PACKAGE_NAME_ARG_KEY, packageName)
                },
                verificationServiceConnection!!.mConnection, Context.BIND_AUTO_CREATE
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return requireNotNull(this.callback)
    }

    private var isServiceBound = false

    private fun onServiceConnected() {
        isServiceBound = true

        val errorCode = paymentServiceConnection?.iPaymentService?.startConnection()

        Log.d(TAG, "onServiceConnected: " + errorCode)

        if (errorCode == ErrorType.NO_ERROR.code) {
            callback?.connectionSucceed?.invoke()
        } else {
            val errorType = ErrorType.values().find {
                it.code == errorCode
            }?.name?.lowercase()
            this.callback?.connectionFailed?.invoke(Throwable("Reason: $errorType"))
        }
    }

    private fun onServiceDisconnected() {
        disconnect()
    }

    override fun purchaseProductBySku(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        payload: String
    ): Bundle {
        if (isServiceBound) {
            val purchaseProductBundle =
                paymentServiceConnection?.iPaymentService?.purchaseProductBySku(
                    sourceSku,
                    userId,
                    purchaseToken,
                    payload
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return purchaseProductBundle
        } else {
            throw ServiceNotInitializedException()
        }
    }

    override fun sendSkuDetails(
        skusBundle: List<Bundle>,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle {
        if (isServiceBound) {
            val skuDetailsBundle =
                paymentServiceConnection?.iPaymentService?.sendSkuDetails(
                    skusBundle, userId, purchaseToken, identifier, payload
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else {
            throw ServiceNotInitializedException()
        }
    }

    override fun sendSkuDetails(
        skusJson: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle {
        if (isServiceBound) {
            val skuDetailsBundle =
                paymentServiceConnection?.iPaymentService?.sendSkuDetailsJson(
                    skusJson, userId, purchaseToken, identifier, payload
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else {
            throw ServiceNotInitializedException()
        }
    }

    override fun purchaseProductViaNetbox(
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle {
        if (isServiceBound) {
            val skuDetailsBundle =
                paymentServiceConnection?.iPaymentService?.purchaseProductViaNetbox(
                    userId, purchaseToken, identifier, payload
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else {
            throw ServiceNotInitializedException()
        }
    }

    private fun disconnect() {
        isServiceBound = false

        runCatching {
            paymentServiceConnection?.iPaymentService?.stopConnection()
        }.onFailure {
            it.printStackTrace()
        }

        runCatching {
            verificationServiceConnection?.mConnection?.let { context.unbindService(it) }
        }.onFailure {
            it.printStackTrace()
        }

        runCatching {
            paymentServiceConnection?.let { context.unbindService(it) }
        }.onFailure {
            it.printStackTrace()
        }
        verificationServiceConnection = null
        paymentServiceConnection = null
        callback?.disconnected?.invoke()
        callback = null
    }

    companion object {
        private const val PAYMENT_SERVICE_ACTION = "ir.net_box.payment.PaymentService.BIND"
        private const val PAYMENT_SERVICE_CLASS_NAME = "ir.net_box.store.payment.sdk.PaymentService"
        private const val PAYMENT_SERVICE_VERIFICATION_CLASS_NAME =
            "ir.net_box.store.payment.sdk.PaymentServiceVerification"
        private const val TAG = "PaymentConnection"
    }
}