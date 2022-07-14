package com.silent.datautils

import com.silent.datautils.DataUtils.encrypt
import com.silent.datautils.DataUtils.toHexString
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun encrypt() {
        println(
            toHexString(
                encrypt(
                    byteArrayOf(1, 2, 3, 4, 5, 6, 7),
                    byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16),
                    "AES/ECB/NoPadding"
                )
            )
        )
    }

    @Test
    fun decrypt() {
        println(toHexString(byteArrayOf(1, 2, 3, 4, 5, 6, 7)))
    }
}