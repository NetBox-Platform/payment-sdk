package ir.net_box.paymentclient.callback

import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.ConnectionState

class ConnectionCallback(private val disconnect: () -> Unit) : Connection {

    private var connectionState: ConnectionState = ConnectionState.Disconnected

    internal var connectionSucceed: () -> Unit = {}

    internal var connectionFailed: (throwable: Throwable) -> Unit = {}

    internal var disconnected: () -> Unit = {}

    fun connectionSucceed(block: () -> Unit) {
        connectionSucceed = {
            connectionState = ConnectionState.Connected
            block()
        }
    }

    fun connectionFailed(block: (throwable: Throwable) -> Unit) {
        connectionFailed = {
            connectionState = ConnectionState.FailedToConnect
            block(it)
        }
    }

    fun disconnected(block: () -> Unit) {
        disconnected = {
            connectionState = ConnectionState.Disconnected
            block()
        }
    }

    override fun getConnectionState(): ConnectionState {
        return connectionState
    }

    override fun disconnect() {
        disconnect.invoke()
    }
}
