package ir.net_box.payment;

import android.os.Bundle;

interface IPaymentService {

    int startConnection();

    Bundle sendSkuDetails(in List<Bundle> skusBundle, String userId, String purchaseToken, String identifier, String payload, String packageName);

    Bundle sendSkuDetailsJson(String skusJson, String userId, String purchaseToken, String identifier, String payload, String packageName);

    Bundle purchaseProductViaNetbox(String userId, String purchaseToken, String identifier, String payload, String packageName);

    Bundle purchaseProductBySku(String sourceSku, String userId, String purchaseToken, String identifier, String payload, String packageName);

    void stopConnection();
}
