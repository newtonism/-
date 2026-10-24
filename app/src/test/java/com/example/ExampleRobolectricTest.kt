package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CeramicCatalog
import com.example.data.PriceFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context and verify catalog`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("일상도자기", appName)

        // Verify all catalog items are affordable everyday ceramics under 20,000 KRW
        assertTrue(CeramicCatalog.products.isNotEmpty())
        assertTrue(CeramicCatalog.products.all { it.price <= 20000 })

        // Verify under 5,000 KRW filter returns budget items
        val under5k = CeramicCatalog.products.filter { it.price <= (PriceFilter.UNDER_5K.maxPrice ?: 0) }
        assertTrue(under5k.isNotEmpty())
    }
}
