package ir.net_box.paymentclient.payment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import ir.net_box.paymentclient.callback.ConnectionCallback
import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.PaymentConnection
import ir.net_box.paymentclient.exception.PaymentException
import ir.net_box.paymentclient.util.isAlreadySucceeded
import ir.net_box.paymentclient.util.isAndroid13OrHigher
import ir.net_box.paymentclient.util.isFailed
import ir.net_box.paymentclient.util.isSucceed

/**
 * @param context The application context
 * @param packageName Your valid application package name
 * Note: Ensure that your package name has been verified by Netbox
 */
class Payment(private val context: Context, private val packageName: String) {

    private val connection = PaymentConnection(context, packageName)

    private var resultBroadcastReceiver: ResultBroadcastReceiver? = null

    private var isReceiverRegistered = false

    /**
     * Establishes a connection to the Netbox payment service.
     * Note: Before performing any other actions, it is essential to connect to the Netbox payment service using this function.
     * @see Connection
     * @param callback Callback used to receive notifications about service connection changes
     * @return A Connection interface that allows you to disconnect from the service or retrieve the current connection state
     */
    fun connect(callback: (ConnectionCallback) -> Unit): Connection {
        return connection.startConnection(callback)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun handlePurchaseResult(
        purchaseProduct: Bundle,
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseCallback = PurchaseCallback().apply(callback)
        when {
            // Case 1: The service returned an immediate synchronous result (Success)
            purchaseProduct.isSucceed() -> {
                purchaseCallback.purchaseSucceed?.let { it(purchaseProduct) }
            }
            // Case 2: The service returned an immediate synchronous result (Already Succeeded)
            purchaseProduct.isAlreadySucceeded() -> {
                purchaseCallback.purchaseIsAlreadySucceeded?.let { it(purchaseProduct) }
            }
            // Case 3: The service returned an immediate synchronous result (Failure)
            purchaseProduct.isFailed() -> {
                purchaseCallback.purchaseFailed?.invoke(
                    PaymentException.PurchaseFailed("Purchase is failed!"), purchaseProduct
                )
            }
            // Case 4: No immediate result. This happens when an Activity was started (Intent flow).
            // We must register a BroadcastReceiver to listen for the result asynchronously.
            else -> {
                if (!isReceiverRegistered) {
                    resultBroadcastReceiver = ResultBroadcastReceiver { intent ->
                        when {
                            intent.isSucceed() -> {
                                purchaseCallback.purchaseSucceed?.let { it(purchaseProduct) }
                            }
                            intent.isAlreadySucceeded() -> {
                                purchaseCallback.purchaseIsAlreadySucceeded?.let {
                                    it(
                                        purchaseProduct
                                    )
                                }
                            }
                            intent.isFailed() -> {
                                purchaseCallback.purchaseFailed?.invoke(
                                    PaymentException.PurchaseFailed("Purchase is failed!"), purchaseProduct
                                )
                            }
                        }
                    }
                    if (isAndroid13OrHigher) {
                        context.registerReceiver(
                            resultBroadcastReceiver,
                            IntentFilter(PAYMENT_BROADCAST_ACTION),
                            Context.RECEIVER_EXPORTED
                        )
                    } else {
                        context.registerReceiver(
                            resultBroadcastReceiver,
                            IntentFilter(PAYMENT_BROADCAST_ACTION)
                        )
                    }
                    isReceiverRegistered = true
                } else {
                    // Receiver already registered, avoiding multiple registrations for same request
                    purchaseCallback.purchaseFailed?.invoke(
                        PaymentException.Unknown("Purchase result is unknown!"), purchaseProduct
                    )
                }
            }
        }
    }

    private inner class ResultBroadcastReceiver(
        val result: (Intent) -> Unit
    ) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                result(intent)
            }
            runCatching {
                context?.unregisterReceiver(resultBroadcastReceiver)
            }.onSuccess {
                isReceiverRegistered = false
            }.onFailure {
                it.printStackTrace()
                isReceiverRegistered = false
            }
        }
    }

    /**
     * Initiates a purchase for a product identified by its SKU.
     *
     * This is the standard method to purchase a pre-defined product.
     *
     * @param sourceSku The SKU of the product to be purchased.
     * @param userId The unique User ID associated with the purchase to sync user-specific data.
     * @param purchaseToken A unique token for this specific purchase request (used for verification).
     * @param identifier Optional identifier to show in the purchase UI (e.g., user's phone/email).
     * @param payload A random string returned in the result bundle to identify the request.
     * @param callback Callback to receive the [PurchaseCallback] for handling results.
     */
    fun purchaseProductBySku(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseProduct =
            connection.purchaseProductBySku(sourceSku, userId, purchaseToken, identifier, payload)
        handlePurchaseResult(purchaseProduct, callback)
    }

    /**
     * Initiates a purchase with explicit pricing.
     *
     * @deprecated Use [purchaseProduct] instead, which supports product types and localized titles.
     *
     * @param sourceSku The SKU to be purchased.
     * @param userId Unique User ID for synchronization.
     * @param purchaseToken Unique token for verification.
     * @param identifier Optional UI identifier (e.g., phone/email).
     * @param payload Request correlation string.
     * @param price Total price in Toman, including VAT.
     * @param discount Discount amount in Toman.
     * @param callback Callback for purchase results.
     */
    @Deprecated(
        message = "Use purchaseProduct() instead. The new version supports productType and localized titles.",
        replaceWith = ReplaceWith(
            expression = "purchaseProduct(sourceSku, userId, purchaseToken, identifier, payload, price, discount, productType, titleFa, titleEn, titleAr, titleTr, callback)",
            imports = ["ir.net_box.store.sdk.ProductType"]
        )
    )
    fun purchaseProductWithPricing(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        price: Int,
        discount: Int,
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseProduct =
            connection.purchaseProductWithPricing(
                sourceSku,
                userId,
                purchaseToken,
                identifier,
                payload,
                price,
                discount
            )
        handlePurchaseResult(purchaseProduct, callback)
    }

    /**
     * Creates a purchase with explicit pricing and localized product metadata.
     *
     * This method supports both subscription and pay‑per‑view product types.
     * After a successful purchase transaction, the SDK automatically triggers the
     * verification API using the provided purchase token.
     *
     * @param sourceSku The unique SKU of the product to be purchased.
     * @param userId The unique user identifier associated with this purchase.
     * Used to synchronize user‑specific purchase data with your backend APIs.
     * (Your pre‑defined APIs will be called with this `userId` if configured.)
     * @param purchaseToken The unique token representing this purchase request.
     * @param identifier An optional identifier shown on the purchase page/UI —
     * for example, a masked phone number or email address.
     * @param payload A random client‑generated string used for request correlation.
     * It will be returned in the result bundle under the key `"payload"`.
     * @param price Original product price in **Toman** (excluding VAT).
     * @param discountedPrice Discounted product price in **Toman** (excluding VAT).
     * If no discount is applied, this should be equal to [price].
     * @param vat VAT amount in **Toman**.
     * 
     * Note: The final amount displayed to the user and charged during checkout is calculated as:
     * `final_price = discountedPrice + vat`
     *
     * @param productType The product type: [ProductType.SUBSCRIPTION] for recurring
     * billing products or [ProductType.PAY_PER_VIEW] for one‑time access items.
     * @param titleFa The product title in **Persian (Farsi)** — *this parameter is required*
     * and used for displaying localized purchase information in the checkout UI.
     * @param titleEn The product title in **English** — optional but **strongly recommended**
     * to enhance internationalized UI and cross‑language presentation.
     * @param titleAr The product title in **Arabic** — optional, recommended for Arabic localizations.
     * @param titleTr The product title in **Turkish** — optional, recommended for Turkish users.
     * @param callback Callback invoked upon completion of the purchase operation,
     * returning a [PurchaseCallback] indicating success, cancellation, or failure.
     */
    fun purchaseProduct(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        price: Int,
        discountedPrice: Int,
        vat: Int,
        productType: ProductType,
        titleFa: String,
        titleEn: String = "",
        titleAr: String = "",
        titleTr: String = "",
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseProduct =
            connection.purchaseProduct(
                sourceSku,
                userId,
                purchaseToken,
                identifier,
                payload,
                price,
                discountedPrice,
                vat,
                productType,
                titleFa,
                titleEn,
                titleAr,
                titleTr
            )
        handlePurchaseResult(purchaseProduct, callback)
    }

    /**
     * Send all SKUs, such as subscription plans, and receive results in a callback.
     * @param skusBundle Contains all SKUs to be sent to the Netbox payment system.
     *                   @see ir.net_box.payment_sample.MainActivity
     * @param userId The unique User ID associated with the purchase to sync user specific data with your pre-defined apis, (We will call your apis (if defined) with this user id)
     * @param purchaseToken The unique token of this purchase request for verification
     * @param identifier An identifier string for the request to show in the purchase page/UI, e.g., user masked phone number or email
     * @param payload A random string used to identify the request, which will be sent back in the bundle with the key named "payload"
     * @param callback Callback to receive the purchase results
     */
    fun sendSkus(
        skusBundle: List<Bundle>,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        callback: (PurchaseCallback) -> Unit
    ) {
        val sendSkus = connection.sendSkuDetails(
            skusBundle, userId, purchaseToken, identifier, payload
        )
        handlePurchaseResult(sendSkus, callback)
    }

    /**
     * Send all SKUs, such as subscription plans, and receive results in a callback.
     * @param skusJson Contains all SKUs in json format.
     *                   @see ir.net_box.payment_sample.MainActivity
     * @param userId The unique User ID associated with the purchase to sync user specific data with your pre-defined apis, (We will call your apis (if defined) with this user id)
     * @param purchaseToken The unique token of this purchase request for verification
     * @param identifier An identifier string for the request to show in the purchase page/UI, e.g., user masked phone number or email
     * @param payload A random string used to identify the request, which will be sent back in the bundle with the key named "payload"
     * @param callback Callback to receive the purchase results
     */
    fun sendSkus(
        skusJson: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        callback: (PurchaseCallback) -> Unit
    ) {
        val sendSkus = connection.sendSkuDetails(
            skusJson, userId, purchaseToken, identifier, payload
        )
        handlePurchaseResult(sendSkus, callback)
    }

    /**
     * Initiates a purchase through the Netbox store interface.
     *
     * This allows the user to select from available products or plans within the Netbox environment.
     *
     * @param userId Unique User ID for synchronization.
     * @param purchaseToken Unique token for verification.
     * @param identifier Optional UI identifier (e.g., phone/email).
     * @param payload Request correlation string.
     * @param callback Callback for purchase results.
     */
    fun purchaseProductViaNetbox(
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseProductViaNetbox = connection.purchaseProductViaNetbox(
            userId, purchaseToken, identifier, payload
        )
        handlePurchaseResult(purchaseProductViaNetbox, callback)
    }

    companion object {
        const val PAYMENT_BROADCAST_ACTION = "ir.net_box.payment.Broadcast"
        const val PAYMENT_INTENT_BROADCAST_ACTION = "ir.net_box.payment.intent.Broadcast"
    }
}
