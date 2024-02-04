package ir.net_box.paymentclient.connection

import android.os.Bundle

interface Payable {

    fun sendSkuDetails(
        skusBundle: List<Bundle>,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    fun sendSkuDetails(
        skusJson: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    fun purchaseProductViaNetbox(
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    fun purchaseProductBySku(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        payload: String
    ): Bundle
}