package ir.net_box.paymentclient.connection

/**
 * Represents the various states of the connection to the Payment service.
 */
sealed class ConnectionState {
    /** Connection is successfully established and ready for use. */
    object Connected : ConnectionState()

    /** Attempt to connect to the service failed. */
    object FailedToConnect : ConnectionState()

    /** The service is currently disconnected. */
    object Disconnected : ConnectionState()
}
