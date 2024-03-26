package ir.net_box.paymentclient.util

import android.content.Intent
import android.os.Bundle

const val ID = "id"
const val TITLE = "title"
const val DESCRIPTION = "description"
const val PRICE = "price"
const val DISCOUNT = "discount"
const val EXTRA = "extra"

const val NETBOX_PAYMENT_RESULT = "netbox_payment_result"
const val PACKAGE_NAME_ARG_KEY = "package_name"
const val PRODUCT_ID_ARG_KEY = "product_id"
const val CALLING_SOURCE_ARG_KEY = "calling_source"
const val PURCHASE_TOKEN_ARG_KEY = "purchase_token"
const val PURCHASE_STATUS_ARG_KEY = "purchase_status"
const val PAYLOAD_ARG_KEY = "payload"
const val USER_ID_ARG_KEY = "user_id"

fun Bundle.isSucceed() =
    getInt(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.SUCCEED.statusCode

fun Bundle.isFailed() =
    getInt(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.FAILED.statusCode

fun Intent.isSucceed() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.SUCCEED.statusCode

fun Intent.isFailed() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.FAILED.statusCode

fun Bundle.toReadableString(): String {
    val stringBuilder = StringBuilder("Result{")
    for (key in keySet()) {
        stringBuilder.append(" $key => ${this[key]};")
    }
    stringBuilder.append(" }")
    return stringBuilder.toString()
}