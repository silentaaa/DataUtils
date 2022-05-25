package com.silent.datautils

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun encrypt() {
        println(DataUtils.toHexString(byteArrayOf(1, 2, 3, 4, 5, 6, 7)))
    }
}