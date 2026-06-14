package ir.net_box.paymentclient.payment

import android.os.Bundle
import ir.net_box.paymentclient.exception.PaymentException

/**
 * Callback class to handle the results of a purchase operation.
 */
class PurchaseCallback {

    internal var purchaseSucceed: ((bundle: Bundle) -> Unit)? = null

    internal var purchaseIsAlreadySucceeded: ((bundle: Bundle) -> Unit)? = null

    internal var purchaseFailed: ((exception: PaymentException, bundle: Bundle) -> Unit)? = null

    /**
     * Sets the callback to be invoked when the purchase is successful.
     *
     * @param block A lambda function that receives a [Bundle] containing purchase details:
     * - `netbox_payment_result`: Status code (1 for success)
     * - `user_id`: The unique User ID provided
     * - `purchase_token`: The unique token for this purchase
     * - `payload`: The client-generated payload string
     * - `source_sku`: The SKU of the purchased product
     */
    fun purchaseSucceed(block: (Bundle) -> Unit) {
        purchaseSucceed = { bundle ->
            block(bundle)
        }
    }

    /**
     * Sets the callback to be invoked when the purchase has already been succeeded previously.
     *
     * @param block A lambda function that receives a [Bundle] containing purchase details
     * (similar to [purchaseSucceed]), with `netbox_payment_result` set to 4.
     */
    fun purchaseIsAlreadySucceeded(block: (Bundle) -> Unit) {
        purchaseIsAlreadySucceeded = { bundle ->
            block(bundle)
        }
    }

    /**
     * Sets the callback to be invoked when the purchase operation fails.
     *
     * @param block A lambda function that receives:
     * - `exception`: The error encountered (e.g., [ir.net_box.paymentclient.exception.ServiceNotInitializedException],
     *   [ir.net_box.paymentclient.exception.PaymentException.SecurityError], or [ir.net_box.paymentclient.exception.PaymentException.PurchaseFailed]).
     * - `bundle`: A [Bundle] containing error details and request context.
     */
    fun purchaseFailed(block: (exception: PaymentException, bundle: Bundle) -> Unit) {
        purchaseFailed = { exception, bundle ->
            block(exception, bundle)
        }
    }
}
