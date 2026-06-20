package ir.net_box.paymentclient.manager

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import ir.net_box.paymentclient.exception.NetStoreNotInstalledException

/**
 * Manager class for handling Netstore application related tasks, such as installation checks
 * and updates.
 */
object AppManager {

    /**
     * Minimum required versions of Netstore for specific payment features.
     */
    enum class PaymentFeatureMinVersion(val versionCode: Int) {
        /** Basic payment support. */
        BASIC_PAYMENT(350),
        /** Support for advanced gateway features and localized products. */
        GATEWAY(360),
        GATEWAY_VAT_INCLUSIVE(371)
    }

    private fun getPackageInfo(context: Context, packageName: String, flags: Int = 0) = try {
        context.packageManager.getPackageInfo(packageName, flags)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Checks if the Netstore application is installed on the device.
     * Also verifies the installation authenticity.
     *
     * @param context The application context.
     * @return True if installed and verified, false otherwise.
     */
    fun isNetstoreInstalled(context: Context): Boolean =
        getPackageInfo(context, NET_STORE_PACKAGE_NAME) != null &&
            Security.verifyNetstoreIsInstalled(context)

    /**
     * Checks if the installed Netstore version needs to be updated to support a specific feature.
     *
     * @param context The application context.
     * @param minStoreVersionCode The minimum version required (defaults to [PaymentFeatureMinVersion.BASIC_PAYMENT]).
     * @return True if an update is required, false otherwise.
     */
    fun shouldUpdateNetstore(
        context: Context,
        minStoreVersionCode: PaymentFeatureMinVersion = PaymentFeatureMinVersion.BASIC_PAYMENT
    ) =
        getNetstoreVersion(context) < minStoreVersionCode.versionCode

    /**
     * Triggers the update process for Netstore.
     * Throws an exception if Netstore is not found.
     *
     * @param context The application context.
     * @throws NetStoreNotInstalledException if Netstore is not installed.
     */
    fun updateNetstore(context: Context) {
        if (getPackageInfo(context, NET_STORE_PACKAGE_NAME) != null) {
            updateNetstoreToLatestVersion(context)
        } else {
            throw NetStoreNotInstalledException()
        }
    }

    /**
     * Redirects the user to the Netstore page for updates using a deep link.
     */
    private fun updateNetstoreToLatestVersion(context: Context) {
        val browserIntent: Intent?
        try {
            browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.store.net_box.ir/store=$NET_STORE_PACKAGE_NAME".toUri()
            )
            browserIntent.setPackage(NET_STORE_PACKAGE_NAME)
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    /** The package name of the Netstore application. */
    const val NET_STORE_PACKAGE_NAME = "ir.net_box.store"
}
