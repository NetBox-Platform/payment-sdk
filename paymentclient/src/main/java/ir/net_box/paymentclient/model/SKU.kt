package ir.net_box.paymentclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SKU(
    /**
     * id of the product
     */
    val id: Int,

    /**
     * Plan title
     * for example:
     نماوا، فیلم نت، فیلیمو
     */
    val title: String?,

    /**
     * Plan description
     * for example:
     اشتراک یکماهه
     */
    val description: String?,

    /**
     * Plan price to show in toman
     * for example: 150000
     */
    val price: Int?,

    /**
     * If your plan have some discount to show, in toman (Optional)
     * for example: 20000
     * Note: Pass this null for no discount
     */
    val discount: Int? = null,

    /**
     * Any extra text (Optional)
     */
    val extra: String? = null,
) : Parcelable
