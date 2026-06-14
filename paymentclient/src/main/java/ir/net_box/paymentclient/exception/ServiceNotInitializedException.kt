package ir.net_box.paymentclient.exception

/**
 * Thrown when an operation is attempted while the payment service connection is not initialized.
 */
class ServiceNotInitializedException : IllegalStateException() {
    override val message: String
        get() = "Netbox service is not initialized!"
}
