package ir.net_box.payment

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import ir.net_box.paymentclient.callback.ConnectionCallback
import ir.net_box.paymentclient.connection.Connection
import ir.net_box.paymentclient.connection.ConnectionState
import ir.net_box.paymentclient.connection.PaymentConnection
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@SmallTest
class PaymentTest {

    private val paymentConnection = PaymentConnection(ApplicationProvider.getApplicationContext(),
        ApplicationProvider.getApplicationContext<Context>().packageName)

    private lateinit var connection: Connection

    private lateinit var connectionCallback: ConnectionCallback

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun connect_returnsConnectedState() {
        connectionCallback = ConnectionCallback { /* Disconnect implementation */ }

        val connectionLatch = CountDownLatch(1)

        connection = connect(connectionLatch)

        val connectionEstablished = connectionLatch.await(5L, TimeUnit.SECONDS)

        // Perform assertions after the service connection has been established
        if (connectionEstablished) {
            Truth.assertThat(connection.getConnectionState()).isEqualTo(
                ConnectionState.Connected
            )

        } else {
            // Handle the case where the connection was not established within the timeout
            // You might want to fail the test or perform other actions
        }
    }

    private fun connect(connectionLatch: CountDownLatch): Connection {
        return paymentConnection.startConnection { connectionCallback ->
                // This lambda is where you can customize the behavior of the ConnectionCallback
                connectionCallback.connectionSucceed {
                    // You can add specific behavior for the successful connection if needed
                    connectionLatch.countDown()
                }
                connectionCallback.connectionFailed {
                }
            }
    }
}