package ir.net_box.paymentclient.connection

import android.os.Bundle
import ir.net_box.paymentclient.payment.ProductType

/**
 * Interface defining the purchase operations supported by the Netbox Payment SDK.
 */
interface Payable {

    /**
     * Sends SKU details to the payment service using a list of Bundles.
     *
     * @param skusBundle List of Bundles containing SKU information.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @return Result Bundle from the service.
     */
    fun sendSkuDetails(
        skusBundle: List<Bundle>,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    /**
     * Sends SKU details to the payment service using a JSON string.
     *
     * @param skusJson JSON string containing SKU details.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @return Result Bundle from the service.
     */
    fun sendSkuDetails(
        skusJson: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    /**
     * Initiates a purchase through the Netbox store application.
     *
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @return Result Bundle from the service.
     */
    fun purchaseProductViaNetbox(
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    /**
     * Purchases a specific product identified by its SKU.
     *
     * @param sourceSku SKU of the product.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @return Result Bundle from the service.
     */
    fun purchaseProductBySku(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String
    ): Bundle

    /**
     * Purchases a product with explicit pricing.
     *
     * @param sourceSku SKU of the product.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @param price Product price in Toman.
     * @param discount Applied discount in Toman.
     * @return Result Bundle from the service.
     */
    fun purchaseProductWithPricing(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String,
        payload: String,
        price: Int,
        discount: Int
    ): Bundle

    /**
     * Purchases a product with pricing and localized metadata.
     *
     * @param sourceSku SKU of the product.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier UI identifier for the request.
     * @param payload Correlation string for the request.
     * @param price Original product price in **Toman** (excluding VAT).
     * @param discountedPrice Discounted product price in **Toman** (excluding VAT).
     * If no discount is applied, this should be equal to [price].
     * @param vat VAT amount in **Toman**.
     * 
     * Note: The final amount displayed to the user and charged during checkout is calculated as:
     * `final_price = discountedPrice + vat`
     *
     * @param productType Type of the product ([ProductType.SUBSCRIPTION] or [ProductType.PAY_PER_VIEW]).
     * @param titleFa Localized title in Persian.
     * @param titleEn Localized title in English.
     * @param titleAr Localized title in Arabic.
     * @param titleTr Localized title in Turkish.
     * @return Result Bundle from the service.
     */
    fun purchaseProduct(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        price: Int,
        discountedPrice: Int,
        vat: Int,
        productType: ProductType,
        titleFa: String,
        titleEn: String = "",
        titleAr: String = "",
        titleTr: String = ""
    ): Bundle
}
