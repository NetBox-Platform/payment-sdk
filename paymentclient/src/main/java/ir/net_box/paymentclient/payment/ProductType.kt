package ir.net_box.paymentclient.payment

enum class ProductType(val value: Int) {
    // A recurring subscription model ((e.g., "plan-3-months").
    SUBSCRIPTION(1),
    // A single-purchase, one-time access model (e.g., pay-per-view).
    PAY_PER_VIEW(2)
}