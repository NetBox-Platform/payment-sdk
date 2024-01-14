package ir.net_box.payment;

import android.os.Bundle;

interface IPaymentService {

    int startConnection();

    Bundle sendSkuDetails(in List<Bundle> skusBundle, String userId, String purchaseToken, String identifier, String payload);

    Bundle sendSkuDetailsJson(String skusJson, String userId, String purchaseToken, String identifier, String payload);

    Bundle purchaseProductViaNetbox(String userId, String purchaseToken, String identifier, String payload);

    Bundle purchaseProductById(int productId, String purchaseToken, String payload);

    void stopConnection();
}
