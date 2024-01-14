package ir.net_box.paymentclient.connection

enum class ErrorType(val code: Int) {
    UNKNOWN(-1),
    NO_ERROR(200),
    SERVER_ERROR(1),
    INTERNET_ERROR(2),
    INVALID_PACKAGE_NAME(3)
}