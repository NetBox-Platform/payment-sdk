package ir.net_box.payment;

import android.os.Bundle;

interface IPaymentService {

    int startConnection();

    Bundle purchaseProductViaNetbox(String userId, String purchaseToken, String identifier, String payload, String packageName);

    Bundle purchaseProductBySku(String sourceSku, String userId, String purchaseToken, String identifier, String payload, String packageName);

    Bundle purchaseProductWithPricing(String sourceSku, String userId, String purchaseToken, String identifier, String payload, String packageName, int price, int discount);

    Bundle purchaseProduct(String sourceSku, String userId, String purchaseToken, String identifier, String payload, String packageName, int price, int discount, int productType, String titleFa, String titleEn, String titleAr, String titleTr);

    Bundle purchaseProductVatInclusive(String sourceSku, String userId, String purchaseToken, String identifier, String payload, String packageName, int price, int discountedPrice, int vat, int productType, String titleFa, String titleEn, String titleAr, String titleTr);

    void stopConnection();
}
