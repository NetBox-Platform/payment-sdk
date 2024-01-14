package ir.net_box.paymentclient.exception

class ServiceNotInitializedException : IllegalStateException() {
    override val message: String
        get() = "Netbox service is not initialized!"
}
