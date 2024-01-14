package ir.net_box.paymentclient.connection

sealed class ConnectionState {
    object Connected : ConnectionState()
    object FailedToConnect : ConnectionState()
    object Disconnected : ConnectionState()
}
