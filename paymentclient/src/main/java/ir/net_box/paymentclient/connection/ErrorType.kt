package ir.net_box.paymentclient.connection

/**
 * Enum representing connection error types returned by the Netbox service.
 *
 * @property code The integer code associated with the error.
 */
enum class ErrorType(val code: Int) {
    /** Unknown or unexpected error. */
    UNKNOWN(-1),
    /** Connection established with no errors. */
    NO_ERROR(200),
    /** Internal server error within Netbox services. */
    SERVER_ERROR(1),
    /** Network connectivity issue. */
    INTERNET_ERROR(2),
    /** The provided package name is not valid or whitelisted. */
    INVALID_PACKAGE_NAME(3)
}
