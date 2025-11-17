package ir.net_box.paymentclient.manager

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object AppManager {

    const val MINIMUM_STORE_VERSION = 336

    private fun getPackageInfo(context: Context, packageName: String, flags: Int = 0) = try {
        context.packageManager.getPackageInfo(packageName, flags)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun isNetstoreInstalled(context: Context): Boolean =
        getPackageInfo(context, NET_STORE_PACKAGE_NAME) != null &&
            Security.verifyNetstoreIsInstalled(context)

    fun shouldUpdateNetstore(context: Context, minStoreVersionCode: Int = MINIMUM_STORE_VERSION) =
        getNetstoreVersion(context) < minStoreVersionCode

    fun updateNetstore(context: Context) {
        if (getPackageInfo(context, NET_STORE_PACKAGE_NAME) != null) {
            updateNetstoreToLatestVersion(context)
        } else {
            throw IllegalStateException("This is not a netbox device or netstore is not installed!")
        }
    }

    /**
     * This function takes the Netstore package and fetches its latest version
     * from the store using a deep link
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

    const val NET_STORE_PACKAGE_NAME = "ir.net_box.store"
}