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
const val NETBOX_PAYMENT_CONNECTION_RESULT = "netbox_payment_connection_result"
const val PACKAGE_NAME_ARG_KEY = "package_name"
const val PRODUCT_ID_ARG_KEY = "product_id"
const val PURCHASE_TOKEN_ARG_KEY = "purchase_token"
const val PAYLOAD_ARG_KEY = "payload"
const val SOURCE_USER_ID_ARG_KEY = "user_id"
const val IDENTIFIER_ARG_KEY = "identifier"
const val SKUS_ARG_KEY = "skus"
const val SOURCE_SKU_ARG_KEY = "source_sku"
const val PRICE_ARG_KEY = "price"
const val DISCOUNT_ARG_KEY = "discount"

const val useBroadCastForPaymentCallbacks = true

fun Bundle.isSucceed() =
    getInt(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.SUCCEED.statusCode

fun Bundle.isAlreadySucceeded() =
    getInt(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.ALREADY_SUCCEEDED.statusCode

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

fun Intent.isAlreadySucceeded() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.ALREADY_SUCCEEDED.statusCode

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