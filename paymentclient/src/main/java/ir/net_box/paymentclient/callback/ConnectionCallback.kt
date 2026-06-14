package ir.net_box.paymentclient.callback

import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.ConnectionState
import ir.net_box.paymentclient.exception.PaymentException

/**
 * Callback class to handle the connection events for the Payment service.
 * Implements the [Connection] interface to allow checking state and disconnecting.
 *
 * @param disconnect Lambda function to be invoked when [disconnect] is called.
 */
class ConnectionCallback(private val disconnect: () -> Unit) : Connection {

    private var connectionState: ConnectionState = ConnectionState.Disconnected

    internal var connectionSucceed: () -> Unit = {}

    internal var connectionFailed: (exception: PaymentException) -> Unit = {}

    internal var disconnected: () -> Unit = {}

    /**
     * Sets the callback to be invoked when the connection to the service is successfully established.
     *
     * @param block A lambda function to be executed on success.
     */
    fun connectionSucceed(block: () -> Unit) {
        connectionSucceed = {
            connectionState = ConnectionState.Connected
            block()
        }
    }

    /**
     * Sets the callback to be invoked when the connection to the service fails.
     *
     * @param block A lambda function that receives a [PaymentException] explaining the failure.
     */
    fun connectionFailed(block: (exception: PaymentException) -> Unit) {
        connectionFailed = {
            connectionState = ConnectionState.FailedToConnect
            block(it)
        }
    }

    /**
     * Sets the callback to be invoked when the service is disconnected.
     *
     * @param block A lambda function to be executed upon disconnection.
     */
    fun disconnected(block: () -> Unit) {
        disconnected = {
            connectionState = ConnectionState.Disconnected
            block()
        }
    }

    /**
     * Returns the current [ConnectionState] of the service.
     */
    override fun getConnectionState(): ConnectionState {
        return connectionState
    }

    /**
     * Disconnects from the payment service.
     */
    override fun disconnect() {
        disconnect.invoke()
    }
}
