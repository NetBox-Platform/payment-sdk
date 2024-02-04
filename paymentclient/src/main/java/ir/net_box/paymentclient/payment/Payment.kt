package ir.net_box.paymentclient.payment

import android.content.Context
import android.os.Bundle
import ir.net_box.paymentclient.callback.ConnectionCallback
import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.PaymentConnection
import ir.net_box.paymentclient.util.isFailed
import ir.net_box.paymentclient.util.isSucceed

/**
 * @param context The application context
 * @param packageName Your valid application package name
 * Note: Ensure that your package name has been verified by Netbox
 */
class Payment(context: Context, private val packageName: String) {

    private val connection = PaymentConnection(context)

    /**
     * Establishes a connection to the Netbox payment service.
     * Note: Before performing any other actions, it is essential to connect to the Netbox payment service using this function.
     * @see Connection
     * @param callback Callback used to receive notifications about service connection changes
     * @return A Connection interface that allows you to disconnect from the service or retrieve the current connection state
     */
    fun connect(callback: (ConnectionCallback) -> Unit): Connection {
        return connection.startConnection(packageName, callback)
    }

    private fun handlePurchaseResult(purchaseProduct: Bundle, callback: (PurchaseCallback) -> Unit) {
        val purchaseCallback = PurchaseCallback().apply(callback)
        when {
            purchaseProduct.isSucceed() -> {
                purchaseCallback.purchaseSucceed?.let { it(purchaseProduct) }
            }
            purchaseProduct.isFailed() -> {
                purchaseCallback.purchaseFailed?.invoke(
                    Throwable("Purchase is failed!"), purchaseProduct
                )
            }
            else -> {
                purchaseCallback.purchaseFailed?.invoke(
                    Throwable("Purchase result is unknown!"), purchaseProduct
                )
            }
        }
    }

    /**
     * Creates a purchase with a specified product ID, using the provided purchase token and payload.
     * After a successful purchase, the verification API is called with the purchase token.
     * @param sourceSku The SKU to be purchased
     * @param userId The unique User ID associated with the purchase to sync user specific data with your pre-defined apis, (We will call your apis (if defined) with this user id)
     * @param purchaseToken The unique token associated with this purchase request
     * @param payload A random string used to identify the request, which will be sent back in the bundle with the key named "payload"
     * @param callback Callback to receive the results of the purchase operation
     */
    fun purchaseProductBySku(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        payload: String,
        callback: (PurchaseCallback) -> Unit
    ) {
        val purchaseProduct = connection.purchaseProductBySku(sourceSku, userId, purchaseToken, payload)
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
     * Initiates the purchase of a product via the Netbox payment service.
     * @param userId The unique User ID associated with the purchase to sync user specific data with your pre-defined apis, (We will call your apis (if defined) with this user id)
     * @param purchaseToken The unique token of this purchase request for verification
     * @param identifier An identifier string for the request to show in the purchase page/UI, e.g., user masked phone number or email
     * @param payload A random string used to identify the request, which will be sent back in the bundle with the key named "payload"
     * @param callback Callback to receive the results of the purchase operation
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
}
