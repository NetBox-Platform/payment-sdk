package ir.net_box.paymentclient.connection

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import ir.net_box.paymentclient.callback.ConnectionCallback
import ir.net_box.paymentclient.exception.ServiceNotInitializedException
import ir.net_box.paymentclient.manager.AppManager.NET_STORE_PACKAGE_NAME
import ir.net_box.paymentclient.payment.Payment.Companion.PAYMENT_INTENT_BROADCAST_ACTION
import ir.net_box.paymentclient.payment.PaymentServiceConnection
import ir.net_box.paymentclient.util.*

class PaymentConnection(
    private val context: Context,
    private val packageName: String
) : Payable {

    private var callback: ConnectionCallback? = null

    private var paymentServiceConnection: PaymentServiceConnection? = null

    private var verificationServiceConnection: PaymentConnectionVerification? = null

    private var isServiceBound = false

    private var shouldUseIntent = false
    private var connectionBroadcastReceiver: ConnectionBroadcastReceiver? = null
    private var isReceiverRegistered = false

    fun startConnection(
        connectionCallback: (ConnectionCallback) -> Unit
    ): Connection {
        Log.d(TAG, "startConnection")

        shouldUseIntent = false

        paymentServiceConnection =
            PaymentServiceConnection(
                ::onServiceConnected,
                ::onServiceDisconnected
            )
        this.callback = ConnectionCallback(disconnect = ::disconnect).apply(connectionCallback)

        verificationServiceConnection = PaymentConnectionVerification(
            packageName,
            {
                // onServiceConnected
                verified ->
                if (verified && !shouldUseIntent) {
                    val flags =
                        if (isAndroid14OrHigher)
                            Context.BIND_AUTO_CREATE or Context.BIND_ALLOW_ACTIVITY_STARTS
                        else
                            Context.BIND_AUTO_CREATE
                    try {
                        // Second try to connect to netbox payment system
                        context.bindService(
                            Intent(PAYMENT_SERVICE_ACTION).apply {
                                `package` = NET_STORE_PACKAGE_NAME
                                setClassName(NET_STORE_PACKAGE_NAME, PAYMENT_SERVICE_CLASS_NAME)
                                putExtra(PACKAGE_NAME_ARG_KEY, packageName)
                            },
                            paymentServiceConnection!!, flags
                        )
                    } catch (e: SecurityException) {
                        this.callback?.connectionFailed?.invoke(e)
                        e.printStackTrace()
                    }
                } else {
                    this.callback?.connectionFailed
                        ?.invoke(Throwable("Connection failed. please try again"))
                }
            }
        ) {
            // onServiceDisconnected
            this.callback?.connectionFailed?.invoke(Throwable("Bad request!"))
        }

        // First we verify your app validation
        try {
            val flags =
                if (isAndroid14OrHigher)
                    Context.BIND_AUTO_CREATE or Context.BIND_ALLOW_ACTIVITY_STARTS
                else
                    Context.BIND_AUTO_CREATE

            val bindService = context.bindService(
                Intent(PAYMENT_SERVICE_ACTION).apply {
                    `package` = NET_STORE_PACKAGE_NAME
                    setClassName(NET_STORE_PACKAGE_NAME, PAYMENT_SERVICE_VERIFICATION_CLASS_NAME)
                    putExtra(PACKAGE_NAME_ARG_KEY, packageName)
                },
                verificationServiceConnection!!.mConnection, flags
            )
            if (!bindService) {
                startConnectionViaIntent()
            }
        } catch (e: SecurityException) {
            // If there's a security exception (e.g., service binding not permitted),
            // fallback to starting the activity via intent as a recovery path-.
            e.printStackTrace()
            startConnectionViaIntent()
        }
        return requireNotNull(this.callback)
    }

    private fun startConnectionViaIntent() {
        shouldUseIntent = true
        context.tryStartActivity(
            getPaymentIntent().apply {
                putExtra(CONNECTION_START, true)
            }
        ) {
            registerConnectionBroadCastIfNeeded()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerConnectionBroadCastIfNeeded() {
        if (connectionBroadcastReceiver == null) {
            connectionBroadcastReceiver = ConnectionBroadcastReceiver { errorCode ->
                if (errorCode == ErrorType.NO_ERROR.code) {
                    this.callback?.connectionSucceed?.invoke()
                } else {
                    val errorType = ErrorType.values().find {
                        it.code == errorCode
                    }?.name?.lowercase()
                    this.callback?.connectionFailed?.invoke(Throwable("Reason: $errorType"))
                }
            }
        }
        if (!isReceiverRegistered) {
            if (isAndroid13OrHigher) {
                context.registerReceiver(
                    connectionBroadcastReceiver,
                    IntentFilter(PAYMENT_INTENT_BROADCAST_ACTION),
                    Context.RECEIVER_EXPORTED
                )
            } else {
                context.registerReceiver(
                    connectionBroadcastReceiver,
                    IntentFilter(PAYMENT_INTENT_BROADCAST_ACTION)
                )
            }
            isReceiverRegistered = true
        }
    }

    private inner class ConnectionBroadcastReceiver(
        val result: (Int) -> Unit
    ) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != PAYMENT_INTENT_BROADCAST_ACTION) return
            result(
                intent.getIntExtra(NETBOX_PAYMENT_CONNECTION_RESULT, -100)
            )
        }
    }

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
        identifier: String,
        payload: String
    ): Bundle {
        if (isServiceBound) {
            val purchaseProductBundle =
                paymentServiceConnection?.iPaymentService?.purchaseProductBySku(
                    sourceSku,
                    userId,
                    purchaseToken,
                    identifier,
                    payload,
                    packageName
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return purchaseProductBundle
        } else if (shouldUseIntent) {
            context.tryStartActivity(
                getPaymentIntent().apply {
                    putExtra(PAYMENT_TYPE, 1)
                    putExtra(
                        PAYMENT_BUNDLE_ARGS,
                        getResultBundle(
                            userId,
                            purchaseToken,
                            identifier,
                            payload,
                            packageName
                        ).apply {
                            putString(SOURCE_SKU_ARG_KEY, sourceSku)
                        }
                    )
                }
            )
            return getResultBundle(userId, purchaseToken, identifier, payload)
        } else {
            throw ServiceNotInitializedException()
        }
    }

    override fun purchaseProductWithPricing(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        price: Int,
        discount: Int
    ): Bundle {
        if (isServiceBound) {
            val purchaseProductBundle =
                paymentServiceConnection?.iPaymentService?.purchaseProductWithPricing(
                    sourceSku,
                    userId,
                    purchaseToken,
                    identifier,
                    payload,
                    packageName,
                    price,
                    discount
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return purchaseProductBundle
        } else if (shouldUseIntent) {
            context.tryStartActivity(
                getPaymentIntent().apply {
                    putExtra(PAYMENT_TYPE, 5)
                    putExtra(
                        PAYMENT_BUNDLE_ARGS,
                        getResultBundle(
                            userId,
                            purchaseToken,
                            identifier,
                            payload,
                            packageName,
                        ).apply {
                            putString(SOURCE_SKU_ARG_KEY, sourceSku)
                            putInt(PRICE_ARG_KEY, price)
                            putInt(DISCOUNT_ARG_KEY, discount)
                        }
                    )
                }
            )
            return getResultBundle(userId, purchaseToken, identifier, payload)
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
                    skusBundle, userId, purchaseToken, identifier, payload, packageName
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else if (shouldUseIntent) {
            context.tryStartActivity(
                getPaymentIntent().apply {
                    putExtra(PAYMENT_TYPE, 3)
                    putExtra(
                        PAYMENT_BUNDLE_ARGS,
                        getResultBundle(
                            userId,
                            purchaseToken,
                            identifier,
                            payload,
                            packageName
                        ).apply {
                            putParcelableArrayList(SKUS_ARG_KEY, ArrayList(skusBundle))
                        }
                    )
                }
            )
            return getResultBundle(userId, purchaseToken, identifier, payload)
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
                    skusJson, userId, purchaseToken, identifier, payload, packageName
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else if (shouldUseIntent) {
            context.tryStartActivity(
                getPaymentIntent().apply {
                    putExtra(PAYMENT_TYPE, 4)
                    putExtra(
                        PAYMENT_BUNDLE_ARGS,
                        getResultBundle(
                            userId,
                            purchaseToken,
                            identifier,
                            payload,
                            packageName
                        ).apply {
                            putString(SKUS_ARG_KEY, skusJson)
                        }
                    )
                }
            )
            return getResultBundle(userId, purchaseToken, identifier, payload)
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
                    userId, purchaseToken, identifier, payload, packageName
                ) ?: run {
                    throw ServiceNotInitializedException()
                }
            return skuDetailsBundle
        } else if (shouldUseIntent) {
            context.tryStartActivity(
                getPaymentIntent().apply {
                    putExtra(PAYMENT_TYPE, 2)
                    putExtra(
                        PAYMENT_BUNDLE_ARGS,
                        getResultBundle(
                            userId,
                            purchaseToken,
                            identifier,
                            payload,
                            packageName
                        )
                    )
                }
            )
            return getResultBundle(userId, purchaseToken, identifier, payload)
        } else {
            throw ServiceNotInitializedException()
        }
    }

    private fun getResultBundle(
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        packageName: String = ""
    ) = Bundle().apply {
        putString(SOURCE_USER_ID_ARG_KEY, userId)
        putString(PURCHASE_TOKEN_ARG_KEY, purchaseToken)
        putString(IDENTIFIER_ARG_KEY, identifier)
        putString(PAYLOAD_ARG_KEY, payload)
        if (packageName.isNotEmpty()) {
            putString(PACKAGE_NAME_ARG_KEY, packageName)
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

        if (shouldUseIntent) {
            shouldUseIntent = false
            context.startActivity(
                getPaymentIntent().apply {
                    putExtra(CONNECTION_END, true)
                }
            )
        }

        runCatching {
            isReceiverRegistered = false
            context.unregisterReceiver(connectionBroadcastReceiver)
        }.onFailure {
            it.printStackTrace()
        }

        connectionBroadcastReceiver = null

        verificationServiceConnection?.clear()
    }

    private fun getPaymentIntent() =
        Intent(PAYMENT_SERVICE_ACTION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            component = ComponentName.unflattenFromString(
                "$NET_STORE_PACKAGE_NAME/$NET_STORE_PACKAGE_NAME" +
                    ".PaymentInitializationActivity"
            )
            `package` = NET_STORE_PACKAGE_NAME
            putExtra(PACKAGE_NAME_ARG_KEY, packageName)
        }

    companion object {
        private const val PAYMENT_SERVICE_ACTION = "ir.net_box.payment.PaymentService.BIND"
        private const val PAYMENT_SERVICE_CLASS_NAME = "ir.net_box.store.payment.sdk.PaymentService"
        private const val PAYMENT_SERVICE_VERIFICATION_CLASS_NAME =
            "ir.net_box.store.payment.sdk.PaymentServiceVerification"
        private const val TAG = "PaymentConnection"

        private const val CONNECTION_START = "payment_init"
        private const val CONNECTION_END = "payment_end"
        private const val PAYMENT_TYPE = "payment_type"
        private const val PAYMENT_BUNDLE_ARGS = "payment_bundle_args"
    }
}