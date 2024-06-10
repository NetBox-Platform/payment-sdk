package ir.net_box.sso.core

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import ir.net_box.paymentclient.manager.AppManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AppManagerTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun isNetStoreInstalled_returnsResult() {
        Truth.assertThat(
            AppManager.isNetstoreInstalled(ApplicationProvider.getApplicationContext())
        )
            .isTrue()
    }
}