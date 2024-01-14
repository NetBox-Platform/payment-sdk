package ir.net_box.payment_sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.gson.Gson
import ir.net_box.payment_sample.databinding.ActivityMainBinding
import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.ConnectionState
import ir.net_box.paymentclient.exception.NetStoreNotInstalledException
import ir.net_box.paymentclient.model.SKU
import ir.net_box.paymentclient.payment.Payment
import ir.net_box.paymentclient.util.DESCRIPTION
import ir.net_box.paymentclient.util.DISCOUNT
import ir.net_box.paymentclient.util.EXTRA
import ir.net_box.paymentclient.util.ID
import ir.net_box.paymentclient.util.PAYLOAD_ARG_KEY
import ir.net_box.paymentclient.util.PRICE
import ir.net_box.paymentclient.util.PRODUCT_ID_ARG_KEY
import ir.net_box.paymentclient.util.PURCHASE_TOKEN_ARG_KEY
import ir.net_box.paymentclient.util.TITLE
import ir.net_box.paymentclient.util.toReadableString
import ir.net_box.sso.core.AppManager

class MainActivity : AppCompatActivity() {

    private lateinit var payment: Payment
    private var connection: Connection? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Checks the installation of the Netstore (Optional).
         * If you intend to use this verification, please include the following dependency:
         * @see https://github.com/NetBox-Platform/sso
         *
         * @throws NetStoreNotInstalledException If Netstore is not installed
         */
        if (!AppManager.isNetstoreInstalled(applicationContext)) {
            throw NetStoreNotInstalledException()
        }

        // Initialize the payment client with the application context and your package name
        // Note: Ensure that your package name has been verified by Netbox
        payment = Payment(context = applicationContext, packageName = packageName)

        connectToNetboxPaymentService()

        binding.apply {
            connectButton.setOnClickListener {
                connectToNetboxPaymentService()
            }
            disconnectButton.setOnClickListener {
                // Use this function to disconnect from the payment service
                connection?.disconnect()
            }

            createSinglePurchaseButton.setOnClickListener {
                if (connection?.getConnectionState() == ConnectionState.Connected) {
                    /**
                     * Create a purchase with a known product ID and receive results in a callback.
                     * In this case, you should display and allow the user to select a subscription plan in your app,
                     * then send the product/plan ID (e.g., 1).
                     */
                    payment.purchaseProductById(
                        productId = 1,
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        payload = "PAYLOAD_123"
                    ) {
                        it.purchaseSucceed {
                            Log.d(TAG, "purchaseSucceed -> productId: " + it.getInt(PRODUCT_ID_ARG_KEY))
                            Log.d(TAG, "purchaseSucceed -> payload: " + it.getString(PAYLOAD_ARG_KEY))
                            Log.d(TAG, "purchaseSucceed -> purchaseToken: " + it.getString(PURCHASE_TOKEN_ARG_KEY))

                            if (it.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "پرداخت موفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseSucceed" + it.toReadableString())
                            }
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(TAG, "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY))
                            Log.d(TAG, "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY))
                            Log.d(TAG, "purchaseFailed -> purchaseToken: " + bundle.getString(PURCHASE_TOKEN_ARG_KEY))

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "عملیات ناموفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseFailed: " + bundle.toReadableString())
                            }
                        }
                    }
                }
            }

            sendNoSkusButton.setOnClickListener {
                /**
                 * Initiates a call to the Netbox payment service to display and handle your SKUs.
                 */
                if (connection?.getConnectionState() == ConnectionState.Connected) {
                    payment.purchaseProductViaNetbox(
                        userId = "USER_ID",
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        identifier = "09123456789",
                        payload = "PAYLOAD_123"
                    ) {
                        it.purchaseSucceed {
                            Log.d(TAG, "purchaseSucceed")
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(TAG, "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY))
                            Log.d(TAG, "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY))
                            Log.d(TAG, "purchaseFailed -> purchaseToken: " + bundle.getString(PURCHASE_TOKEN_ARG_KEY))

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "عملیات ناموفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseFailed: " + bundle.toReadableString())
                            }                        }
                    }
                }
            }
        }
    }

    // Connect to the Netbox payment system and receive connection callbacks
    private fun connectToNetboxPaymentService() {
        connection = payment.connect {
            it.connectionSucceed {
                // Netbox payment service is ready to use!
                Log.d(TAG, "You are Connected to netbox payment service")
                binding.connectionStateText.setText(
                    R.string.service_connection_connected
                )
            }
            it.connectionFailed {
                // Note: Ensure that your app has the necessary Netbox payment permissions
                Log.d(TAG, it.message.toString())
                binding.connectionStateText.setText(
                    R.string.service_connection_failed
                )
            }
            it.disconnected {
                Log.d(TAG, "You are disconnected from netbox payment service")
                binding.connectionStateText.setText(
                    R.string.service_connection_not_connected
                )
            }
        }
    }

    companion object {
        const val TAG = "NetboxPayment"
    }
}