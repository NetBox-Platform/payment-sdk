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

    <uses-permission android:name="ir.net_box.permission.PAYMENT"/>
    
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
```kotlin
    import ir.net_box.paymentclient.payment.Payment

    // Initialize the Payment class with the application context and your package name
    val payment = Payment(context = context, packageName = packageName)
```

## Connection
```kotlin
    import ir.net_box.paymentclient.exception.PaymentException

    // Establish a connection to the Netbox payment service
    val connection = payment.connect { callback ->
      callback.connectionSucceed {
          // Connection to Netbox payment service succeeded
      }
      callback.connectionFailed { exception ->
          // Connection to Netbox payment service failed
          // exception is of type PaymentException
      }
      callback.disconnected {
          // Disconnected from Netbox payment service
      }
    }
```

## Purchase
### Purchase a product by Source SKU
```kotlin
    /*
     * @param sourceSku The SKU to be purchased (e.g., "plan-3-months")
     * @param userId Unique User ID for synchronization
     * @param purchaseToken Unique token for this purchase request
     * @param payload Request correlation string
     * @param callback Callback to receive results
     */
    payment.purchaseProductBySku(
	    sourceSku = "test-sku",
	    userId = "YOUR_UNIQUE_USER_ID",
        purchaseToken = "YOUR_PURCHASE_TOKEN",
        payload = "PAYLOAD_123"
    ) { purchaseCallback ->
		purchaseCallback.purchaseSucceed { bundle ->
		    // Handle successful purchase
		}
		purchaseCallback.purchaseIsAlreadySucceeded { bundle ->
		    // Handle already succeeded purchase
		}
		purchaseCallback.purchaseFailed { exception, bundle ->
		    // Handle failed purchase (exception is PaymentException)
		}
    }
```

### Purchase a product
```kotlin
      import ir.net_box.paymentclient.payment.ProductType

      /*
      * Purchase a product with pricing and multilanguage titles.
      * Supports both subscription and pay‑per‑view product types.
      *
      * @param sourceSku Product SKU
      * @param userId Unique User ID
      * @param purchaseToken Unique purchase token
      * @param identifier Optional UI identifier (e.g., masked phone number)
      * @param payload Request correlation string
      * @param price Original product price in **Toman** (excluding VAT)
      * @param discountedPrice Discounted product price in **Toman** (excluding VAT).
      * If no discount is applied, this should be equal to `price`.
      * @param vat VAT amount in **Toman**
      *
      * Note: The final amount displayed to the user and charged during checkout is calculated as:
      * `final_price = discountedPrice + vat`
      *
      * @param productType [ProductType.SUBSCRIPTION] or [ProductType.PAY_PER_VIEW]
      * @param titleFa Persian title (Required)
      * @param titleEn English title (Optional but recommended)
      * @param titleAr Arabic title (Optional)
      * @param titleAr Turkish title (Optional)
      * @param callback Result callback
      */
      payment.purchaseProduct(
          sourceSku = "the_jackal_s01e01_sku", 
          userId = "YOUR_UNIQUE_USER_ID",
          purchaseToken = "YOUR_PURCHASE_TOKEN",
          identifier = "09123456789",
          payload = "PAYLOAD_123",
          price = 200000, 
          discountedPrice = 170000,
          vat = 17000,
          productType = ProductType.PAY_PER_VIEW,
          titleFa = "شغال - قسمت اول فصل اول",
          titleEn = "The Jackal - Season 1 Episode 1",
          titleAr = "ابن آوى – الحلقة الأولى من الموسم الأول",
          titleTr = "Çakal – 1.Sezon 1.Bölüm"
          ) { purchaseCallback ->
              purchaseCallback.purchaseSucceed { bundle ->
                  // Handle successful purchase
              }
              purchaseCallback.purchaseIsAlreadySucceeded { bundle ->
                  // Handle already succeeded purchase
              }
              purchaseCallback.purchaseFailed { exception, bundle ->
                  // Handle failed purchase (exception is PaymentException)
              }
          }
```

### Via Netbox
> **Highly Recommended**: Use this method for subscription plans to ensure the best user experience and compatibility with Netbox-managed SKUs.

```kotlin
     /*
      * Initiates a call to Netbox to display and handle your SKUs.
      */
     payment.purchaseProductViaNetbox(
            userId = "YOUR_UNIQUE_USER_ID",
            purchaseToken = "YOUR_PURCHASE_TOKEN",
            identifier = "09123456789",
            payload = "PAYLOAD_123"
        )  { purchaseCallback ->
             purchaseCallback.purchaseSucceed { bundle ->
                 // Handle successful purchase
             }
             purchaseCallback.purchaseIsAlreadySucceeded { bundle ->
                 // Handle already succeeded purchase
             }
             purchaseCallback.purchaseFailed { exception, bundle ->
                 // Handle failed purchase (exception is PaymentException)
             }
        }
```

## Disconnect
```kotlin
    // Disconnect from the service
    connection?.disconnect()
```

## Handling Results & Exceptions

### Purchase Callback Handlers
- `purchaseSucceed { bundle -> ... }`: Called upon successful transaction.
- `purchaseIsAlreadySucceeded { bundle -> ... }`: Called if the product is already owned.
- `purchaseFailed { exception, bundle -> ... }`: Called when an error occurs.

### Result Bundle Content
The `Bundle` object returned in all callbacks contains the following data. You can use the constants from `ir.net_box.paymentclient.util.*` instead of hardcoded strings:

- `NETBOX_PAYMENT_RESULT`: (Int) Result status (1: Success, 2: Failed, 4: Already Succeeded).
- `SOURCE_USER_ID_ARG_KEY`: (String) The user identifier provided in the request.
- `PURCHASE_TOKEN_ARG_KEY`: (String) The unique token for this purchase.
- `PAYLOAD_ARG_KEY`: (String) The correlation string provided in the request.
- `SOURCE_SKU_ARG_KEY`: (String) The SKU of the product.

### Handling PaymentException
The `exception` parameter in `connectionFailed` and `purchaseFailed` is of type `PaymentException`. You can handle different error scenarios as follows:

```kotlin
    import ir.net_box.paymentclient.exception.PaymentException

    purchaseCallback.purchaseFailed { exception, bundle ->
        when (exception) {
            is PaymentException.ConnectionFailed -> { 
                // Error during service connection. Check exception.errorType
                val reason = exception.message 
            }
            is PaymentException.SecurityError -> { 
                // Permission or binding issues
            }
            is PaymentException.PurchaseFailed -> { 
                // General purchase failure
            }
            is PaymentException.BadRequest -> { 
                // Request rejected by the service
            }
        }
    }
```

### Pre-checks (Netstore update)
It is essential to check if Netstore is installed and updated to support the features you are using.

```kotlin
    import ir.net_box.paymentclient.manager.AppManager
	
	if (!AppManager.isNetstoreInstalled(context)) return
	
	// Check for the minimum version required for your features:
    // - BASIC_PAYMENT: For standard SKU-based purchases.
    // - GATEWAY_VAT_INCLUSIVE: For explicit VAT and discounted price handling.
	if (AppManager.shouldUpdateNetstore(context, AppManager.PaymentFeatureMinVersion.GATEWAY_VAT_INCLUSIVE)) {
		AppManager.updateNetstore(context)
		return
	}
```

## Full examples are available in the links below:
[Sample1](https://github.com/NetBox-Platform/payment-sdk/blob/main/sample/src/main/java/ir/net_box/payment_sample/MainActivity.kt)
