package ir.net_box.paymentclient.exception

class NetStoreNotInstalledException : IllegalStateException() {
    override val message: String
        get() = "Netstore is not installed"
}
