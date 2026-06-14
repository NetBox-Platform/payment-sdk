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
     * The original price of the plan in **Toman** (excluding VAT).
     * Example: 150000
     */
    val price: Int?,

    /**
     * The discounted price of the plan in **Toman** (excluding VAT).
     * If no discount is applied, this should be equal to [price].
     */
    val discountedPrice: Int?,

    /**
     * The VAT amount for this plan in **Toman**.
     * 
     * Note: The final amount displayed to the user and charged during checkout is calculated as:
     * `final_price = discountedPrice + vat`
     */
    val vat: Int?,

    /**
     * Any extra text (Optional)
     */
    val extra: String? = null,
) : Parcelable
