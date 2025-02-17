package ir.net_box.paymentclient.payment

import android.os.Bundle

class PurchaseCallback {

    internal var purchaseSucceed: ((bundle: Bundle) -> Unit)? = null

    internal var purchaseIsAlreadySucceeded: ((bundle: Bundle) -> Unit)? = null

    internal var purchaseFailed: ((throwable: Throwable, bundle: Bundle) -> Unit)? = null

    fun purchaseSucceed(block: (Bundle) -> Unit) {
        purchaseSucceed = { bundle ->
            block(bundle)
        }
    }

    fun purchaseIsAlreadySucceeded(block: (Bundle) -> Unit) {
        purchaseIsAlreadySucceeded = { bundle ->
            block(bundle)
        }
    }

    fun purchaseFailed(block: (throwable: Throwable, bundle: Bundle) -> Unit) {
        purchaseFailed = { throwable, bundle ->
            block(throwable, bundle)
        }
    }
}
