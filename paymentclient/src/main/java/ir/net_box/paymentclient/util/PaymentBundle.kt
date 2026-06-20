package ir.net_box.paymentclient.util

import android.content.Intent
import android.os.Bundle

const val NETBOX_PAYMENT_RESULT = "netbox_payment_result"
const val NETBOX_PAYMENT_CONNECTION_RESULT = "netbox_payment_connection_result"
const val PACKAGE_NAME_ARG_KEY = "package_name"
const val PRODUCT_ID_ARG_KEY = "product_id"
const val PURCHASE_TOKEN_ARG_KEY = "purchase_token"
const val PAYLOAD_ARG_KEY = "payload"
const val SOURCE_USER_ID_ARG_KEY = "user_id"
const val IDENTIFIER_ARG_KEY = "identifier"
const val SOURCE_SKU_ARG_KEY = "source_sku"
const val PRICE_ARG_KEY = "price"
const val DISCOUNT_ARG_KEY = "discount"
const val DISCOUNTED_PRICE_ARG_KEY = "discounted_price"
const val VAT_ARG_KEY = "vat"
const val PRODUCT_TYPE_ARG_KEY = "product_type"
const val TITLE_FA_ARG_KEY = "title_fa"
const val TITLE_EN_ARG_KEY = "title_en"
const val TITLE_AR_ARG_KEY = "title_ar"
const val TITLE_TR_ARG_KEY = "title_tr"

/**
 * Checks if the bundle indicates a successful purchase operation.
 */
fun Bundle.isSucceed() =
    getInt(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.SUCCEED.statusCode

/**
 * Checks if the bundle indicates the purchase was already successful previously.
 */
fun Bundle.isAlreadySucceeded() =
    getInt(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.ALREADY_SUCCEEDED.statusCode

/**
 * Checks if the bundle indicates a failed purchase operation.
 */
fun Bundle.isFailed() =
    getInt(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.FAILED.statusCode

/**
 * Checks if the intent extras indicate a successful purchase operation.
 */
fun Intent.isSucceed() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.SUCCEED.statusCode

/**
 * Checks if the intent extras indicate the purchase was already successful previously.
 */
fun Intent.isAlreadySucceeded() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT, ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.ALREADY_SUCCEEDED.statusCode

/**
 * Checks if the intent extras indicate a failed purchase operation.
 */
fun Intent.isFailed() =
    getIntExtra(
        NETBOX_PAYMENT_RESULT,
        ServiceResultStatus.UNKNOWN.statusCode
    ) == ServiceResultStatus.FAILED.statusCode

/**
 * Extension function to convert a Bundle to a human-readable string representation for logging.
 */
fun Bundle.toReadableString(): String {
    val stringBuilder = StringBuilder("Result{")
    for (key in keySet()) {
        stringBuilder.append(" $key => ${this[key]};")
    }
    stringBuilder.append(" }")
    return stringBuilder.toString()
}