package ir.net_box.paymentclient.connection

interface Connection {
    /**
     * Use this function to disconnect from the payment service.
     */
    fun disconnect()

    /**
     * Use this function to get notified about the payment service's connection state.
     * @return ConnectionState which represents the current state of the payment service.
     * @see ConnectionState
     */
    fun getConnectionState(): ConnectionState
}