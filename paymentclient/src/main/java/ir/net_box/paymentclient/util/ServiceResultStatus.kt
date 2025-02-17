package ir.net_box.paymentclient.util

enum class ServiceResultStatus(val statusCode: Int) {
    SUCCEED(1),
    FAILED(2),
    UNKNOWN(3),
    ALREADY_SUCCEEDED(4),
}
