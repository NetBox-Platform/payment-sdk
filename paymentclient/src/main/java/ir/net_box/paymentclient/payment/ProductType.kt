package ir.net_box.paymentclient.payment

/**
 * Enum representing the types of products available for purchase.
 *
 * @property value The integer value associated with the product type, used for internal signaling.
 */
enum class ProductType(val value: Int) {
    /**
     * A recurring subscription model (e.g., "plan-3-months").
     * These products typically grant access for a specific duration and may auto-renew.
     */
    SUBSCRIPTION(1),

    /**
     * A single-purchase, one-time access model (e.g., pay-per-view).
     * Once purchased, the user typically has permanent or one-time access to the content.
     */
    PAY_PER_VIEW(2)
}
