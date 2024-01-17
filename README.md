[![](https://jitpack.io/v/NetBox-Platform/payment-sdk.svg)](https://jitpack.io/#NetBox-Platform/payment-sdk)


# Netbox Payment System
The Netbox Payment System is a payment and in-app billing SDK designed for integration into third-party apps. This SDK provides a wide range of in-app payment solutions through seamless integration with Netbox services.
With its features, developers can enhance their applications by facilitating secure and efficient payment transactions.

## Features
In-App Payments: Enable various types of in-app payments, including subscriptions, one-time purchases, and more.

Netbox Integration: Leverage the power of Netbox services for seamless and reliable payment processing.

Flexibility: The SDK offers flexibility for developers to tailor the payment experience to the unique requirements of their apps.

## Getting Started
To start using the Netbox Payment System in your app:

- Ensure that your app is whitelisted by Netbox. If not, [contact us](https://netbox.info/contact-netbox/) to initiate the cooperation process.

- Integrate the Netbox Payment SDK into your app.

- Follow the provided documentation to configure and customize the payment functionalities according to your app's needs.

### Permission
Add netbox payment permission 

	<permission android:name="ir.net_box.permission.PAYMENT"/>

### Dependency

Step 1. Add jitpack repository

Legacy:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


New gradle:

    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
    			...
       			mavenCentral()
    			maven { url 'https://jitpack.io' }
        }
    }

Step 2. Add the dependency

	dependencies {
 		implementation 'com.github.NetBox-Platform:payment-sdk:<latest-version>'
	}
	
# Usage
## Initialization
    // Initialize the Payment class with the application context and your package name
    val payment = Payment(context = context, packageName = packageName)
## Connection
    // Establish a connection to the Netbox payment service
    val connection = payment.connect { callback ->
      callback.connectionSucceed {
          // Connection to Netbox payment service succeeded
      }
      callback.connectionFailed { throwable ->
          // Connection to Netbox payment service failed
      }
      callback.disconnected {
          // Disconnected from Netbox payment service
      }
    }
  
## Purchase
### Purchase a product by ID

    payment.purchaseProductById(
        productId = YOUR_PRODUCT_ID,
        purchaseToken = "YOUR_PURCHASE_TOKEN",
        payload = "PAYLOAD_123"
    ) { purchaseCallback ->
        purchaseCallback.purchaseSucceed { bundle ->
            // Handle successful purchase
        }
        purchaseCallback.purchaseFailed { throwable, bundle ->
            // Handle failed purchase
        }
    }

### Via Netbox

    //Initiates a call to the Netbox payment service to display and handle your SKUs.
     payment.purchaseProductViaNetbox(
            userId = "USER_ID",
            purchaseToken = "YOUR_PURCHASE_TOKEN",
            identifier = "09123456789",
            payload = "PAYLOAD_123"
        )  { purchaseCallback ->
          // Handle purchase results
        }


## Disconnect

    // Disconnect from the Netbox payment service
    connection?.disconnect()

    
## Full examples are available in the links below:
[Sample1](https://github.com/NetBox-Platform/payment-sdk/blob/main/sample/src/main/java/ir/net_box/payment_sample/MainActivity.kt)
