package ir.net_box.payment_sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.net_box.payment_sample.databinding.ActivityMainBinding
import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.ConnectionState
import ir.net_box.paymentclient.manager.AppManager
import ir.net_box.paymentclient.payment.Payment
import ir.net_box.paymentclient.payment.ProductType
import ir.net_box.paymentclient.util.PAYLOAD_ARG_KEY
import ir.net_box.paymentclient.util.PRODUCT_ID_ARG_KEY
import ir.net_box.paymentclient.util.PURCHASE_TOKEN_ARG_KEY
import ir.net_box.paymentclient.util.toReadableString

class MainActivity : AppCompatActivity() {

    private lateinit var payment: Payment
    private var connection: Connection? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Checks the installation of the Netstore.
         */
        if (!AppManager.isNetstoreInstalled(applicationContext)) {
            // Netstore is not installed, so you can not use the netbox payment service
            return
        }

        /**
         * You can check for updates to the netstore that supports the payment service.
         */
        if (AppManager.shouldUpdateNetstore(
                this,
                AppManager.PaymentFeatureMinVersion.BASIC_PAYMENT
            )
        ) {
            // Show a dialog to the user to update the netstore
            AppManager.updateNetstore(this)
            return
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
                // Check if the connection is established before making a purchase
                if (connection?.getConnectionState() == ConnectionState.Connected) {
                    /**
                     * Create a purchase with a known product ID and receive results in a callback.
                     * In this case, you should display and allow the user to select a subscription plan in your app,
                     * then send your sku (e.g., "plan-3-months").
                     */
                    payment.purchaseProductBySku(
                        sourceSku = "test-sku",
                        userId = "YOUR_UNIQUE_USER_ID",
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        identifier = "09123456789", // Optional                        
                        payload = "PAYLOAD_123"
                    ) {
                        it.purchaseSucceed {
                            Log.d(
                                TAG,
                                "purchaseSucceed -> productId: " + it.getInt(PRODUCT_ID_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseSucceed -> payload: " + it.getString(PAYLOAD_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseSucceed -> purchaseToken: " + it.getString(
                                    PURCHASE_TOKEN_ARG_KEY
                                )
                            )

                            if (it.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "پرداخت موفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseSucceed" + it.toReadableString())
                            }
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(
                                TAG,
                                "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> purchaseToken: " + bundle.getString(
                                    PURCHASE_TOKEN_ARG_KEY
                                )
                            )

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(
                                    this@MainActivity,
                                    "عملیات ناموفق",
                                    Toast.LENGTH_LONG
                                )
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
                        userId = "YOUR_UNIQUE_USER_ID",
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        identifier = "09123456789",
                        payload = "PAYLOAD_123"
                    ) {
                        it.purchaseSucceed {
                            Log.d(TAG, "purchaseSucceed")
                            if (it.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "پرداخت موفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseSucceed" + it.toReadableString())
                            }
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(
                                TAG,
                                "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> purchaseToken: " + bundle.getString(
                                    PURCHASE_TOKEN_ARG_KEY
                                )
                            )

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(
                                    this@MainActivity,
                                    "عملیات ناموفق",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                Log.d(TAG, "purchaseFailed: " + bundle.toReadableString())
                            }
                        }
                    }
                }
            }

            purchaseWithPricingButton.setOnClickListener {
                /**
                 * Create a purchase for a specific product and receive the result through a callback.
                 * For Pay‑Per‑View or subscriptions, display available plans in your UI,
                 * then pass the selected SKU, item price (including VAT), and discount values.
                 */
                if (connection?.getConnectionState() == ConnectionState.Connected) {
                    payment.purchaseProduct(
                        sourceSku = "plan-3-months",
                        userId = "YOUR_UNIQUE_USER_ID",
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        identifier = "09123456789",
                        payload = "PAYLOAD_123",
                        price = 220000, // Price in Toman
                        discount = 30000, // Discount in Toman
                        productType = ProductType.SUBSCRIPTION,
                        titleFa = "اشتراک سه ماهه",
                        titleEn = "Three‑month subscription",
                        titleAr = "اشتراك لمدة ثلاثة أشهر",
                        titleTr = "Üç aylık abonelik"
                    ) {
                        it.purchaseSucceed {
                            Log.d(TAG, "purchaseSucceed")
                            if (it.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "پرداخت موفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseSucceed" + it.toReadableString())
                            }
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(
                                TAG,
                                "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> purchaseToken: " + bundle.getString(
                                    PURCHASE_TOKEN_ARG_KEY
                                )
                            )

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(
                                    this@MainActivity,
                                    "عملیات ناموفق",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                Log.d(TAG, "purchaseFailed: " + bundle.toReadableString())
                            }
                        }
                    }
                }
            }

            purchasePayPerViewButton.setOnClickListener {
                /**
                 * Performs a Pay‑Per‑View purchase for a specific product.
                 *
                 * This example shows how to call [purchaseProduct] when the
                 * connection is established and handle both success and failure results.
                 *
                 * - **Price & Discount:** in Toman.
                 * - **Product Type:** [ProductType.PAY_PER_VIEW]
                 * - **Required:** `titleFa`
                 * - **Optional but strongly recommended:** `titleEn`, `titleAr`, `titleTr` (for showing product titles in the appropriate language based on app locale)
                 */
                if (connection?.getConnectionState() == ConnectionState.Connected) {
                    payment.purchaseProduct(
                        sourceSku = "the_jackal_s01e01_sku",
                        userId = "YOUR_UNIQUE_USER_ID",
                        purchaseToken = "YOUR_PURCHASE_TOKEN",
                        identifier = "09123456789",
                        payload = "PAYLOAD_123",
                        price = 30000, // Price in Toman (including VAT)
                        discount = 5000, // Discount in Toman
                        productType = ProductType.PAY_PER_VIEW,
                        titleFa = "شغال - قسمت اول فصل اول",
                        titleEn = "The Jackal - Season 1 Episode 1",
                        titleAr = "ابن آوى – الحلقة الأولى من الموسم الأول",
                        titleTr = "Çakal – 1.Sezon 1.Bölüm"
                    ) {
                        it.purchaseSucceed {
                            Log.d(TAG, "purchaseSucceed")
                            if (it.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(this@MainActivity, "پرداخت موفق", Toast.LENGTH_LONG)
                                    .show()
                                Log.d(TAG, "purchaseSucceed" + it.toReadableString())
                            }
                        }

                        it.purchaseFailed { throwable, bundle ->
                            Log.d(TAG, "purchaseFailed: " + throwable.message)

                            Log.d(
                                TAG,
                                "purchaseFailed -> productId: " + bundle.getInt(PRODUCT_ID_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> payload: " + bundle.getString(PAYLOAD_ARG_KEY)
                            )
                            Log.d(
                                TAG,
                                "purchaseFailed -> purchaseToken: " + bundle.getString(
                                    PURCHASE_TOKEN_ARG_KEY
                                )
                            )

                            if (bundle.getString(PAYLOAD_ARG_KEY) == "PAYLOAD_123") {
                                // Valid result
                                Toast.makeText(
                                    this@MainActivity,
                                    "عملیات ناموفق",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                Log.d(TAG, "purchaseFailed: " + bundle.toReadableString())
                            }
                        }
                    }
                }
            }
        }
    }

    // Connect to the Netbox payment system and receive connection callbacks
    private fun connectToNetboxPaymentService() {
        connection = payment.connect {
            it.connectionSucceed {
                Log.d(TAG, "You are Connected to netbox payment service")
                /* Netbox payment service is ready to use!
                 You can call the purchase functions here safely
                 No need to check the connection state */
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
