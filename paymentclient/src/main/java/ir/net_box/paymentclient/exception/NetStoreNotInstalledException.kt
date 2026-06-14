package ir.net_box.paymentclient.exception

/**
 * Thrown when the Netstore application is not installed on the device.
 */
class NetStoreNotInstalledException : IllegalStateException() {
    override val message: String
        get() = "Netstore is not installed"
}
