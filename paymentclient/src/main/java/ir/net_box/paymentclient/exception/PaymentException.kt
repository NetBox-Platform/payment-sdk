package ir.net_box.paymentclient.exception

import ir.net_box.paymentclient.connection.ErrorType

/**
 * Base class for all exceptions thrown by the Netbox Payment SDK.
 */
sealed class PaymentException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /**
     * Thrown when there is an error connecting to the Netbox service.
     * 
     * @property errorType The specific type of error returned by the service.
     */
    class ConnectionFailed(val errorType: ErrorType) :
        PaymentException("Connection failed: ${errorType.name.lowercase().replace('_', ' ')}")

    /**
     * Thrown when a security error occurs, such as when the application
     * doesn't have the necessary permissions to bind to the service.
     */
    class SecurityError(cause: Throwable) :
        PaymentException("Security error occurred while connecting to service", cause)

    /**
     * Thrown when the connection request is rejected by the service as a "Bad Request".
     */
    class BadRequest : PaymentException("Bad request to the payment service")

    /**
     * Thrown when a purchase operation fails.
     * 
     * @property reason Descriptive message of why the purchase failed.
     */
    class PurchaseFailed(reason: String) : PaymentException(reason)

    /**
     * Thrown when an unknown error occurs.
     */
    class Unknown(message: String = "Unknown payment error") : PaymentException(message)
}
