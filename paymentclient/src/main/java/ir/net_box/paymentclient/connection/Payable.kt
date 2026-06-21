package ir.net_box.paymentclient.connection

import android.os.Bundle
import ir.net_box.paymentclient.payment.ProductType

/**
 * Interface defining the purchase operations supported by the Netbox Payment SDK.
 */
interface Payable {

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
    @Deprecated("Use purchaseSingleProduct for the new VAT-exclusive pricing logic.")
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
     * @param price Product price in Toman.
     * @param discount Applied discount in Toman.
     * @param productType Type of the product ([ProductType.SUBSCRIPTION] or [ProductType.PAY_PER_VIEW]).
     * @param titleFa Localized title in Persian.
     * @param titleEn Localized title in English.
     * @param titleAr Localized title in Arabic.
     * @param titleTr Localized title in Turkish.
     * @return Result Bundle from the service.
     */
    @Deprecated("Use purchaseSingleProduct for the new VAT-exclusive pricing logic.")
    fun purchaseProduct(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        price: Int,
        discount: Int,
        productType: ProductType,
        titleFa: String,
        titleEn: String = "",
        titleAr: String = "",
        titleTr: String = ""
    ): Bundle

    /**
     * Purchases a product with pricing and localized metadata using the new VAT-exclusive logic.
     *
     * @param sourceSku Product SKU.
     * @param userId Unique identifier for the user.
     * @param purchaseToken Token for the purchase request.
     * @param identifier Optional UI identifier for the request (e.g., masked phone number).
     * @param payload Correlation string for the request.
     * @param price Original product price in **Toman** (excluding VAT).
     * @param discount Applied discount in **Toman** (excluding VAT). If no discount is applied, this value should be `0`.
     * @param productType Type of the product ([ProductType.SUBSCRIPTION] or [ProductType.PAY_PER_VIEW]).
     * @param titleFa Localized title in Persian (Required).
     * @param titleEn Localized title in English (Optional but recommended).
     * @param titleAr Localized title in Arabic (Optional).
     * @param titleTr Localized title in Turkish (Optional).
     *
     * @return Result Bundle from the service.
     *
     * Note: The final price charged to the user is calculated as:
     * 1. `discounted_price = price - discount`
     * 2. `vat = discounted_price * 0.1`
     * 3. `final_price = discounted_price + vat`
     * (Note: The 10% VAT rate is subject to change based on current regulations.)
     */
    fun purchaseSingleProduct(
        sourceSku: String,
        userId: String,
        purchaseToken: String,
        identifier: String = "",
        payload: String,
        price: Int,
        discount: Int,
        productType: ProductType,
        titleFa: String,
        titleEn: String = "",
        titleAr: String = "",
        titleTr: String = ""
    ): Bundle
}
