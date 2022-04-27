package com.silent.datautils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object DataUtils {

    fun getFileMD5(file: File): String? {
        if (!file.isFile) {
            return null
        }
        val digest: MessageDigest
        val inputStream: FileInputStream
        val buffer = ByteArray(1024)
        var len: Int
        try {
            digest = MessageDigest.getInstance("MD5")
            inputStream = FileInputStream(file)
            while (inputStream.read(buffer, 0, 1024).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return bytesToHexString(digest.digest())
    }

    /**
     * 将字节数组转换为16进制的字符串
     */
    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder()
        if (src == null || src.isEmpty()) {
            return null
        }
        for (b in src) {
            val v: Int = b.toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }

    /**
     * 将字符串转换为字节数组，转换规则：两位字符组成一个字节
     */
    fun stringToByteArray(data: String): ByteArray {
        val charArray = data.toCharArray()
        val byteArray = mutableListOf<Byte>()
        val tmpCharArray = mutableListOf<Char>()
        charArray.forEachIndexed { index, value ->
            if (index % 2 != 0) {
                tmpCharArray.add(value)
                byteArray.add(String(tmpCharArray.toCharArray()).toInt(16).toByte())
                tmpCharArray.clear()
            } else {
                tmpCharArray.add(value)
            }
        }
        return byteArray.toByteArray()
    }

    /**
     * 转换为2进制字符数组，最大支持16位
     */
    fun toBitArray(data: Int, totalBit: Int): CharArray {
        val zero = "0000000000000000"
        var binStr = (data and if (totalBit > 8) 0xFFFF else 0xFF).toString(2)
        if (binStr.length < totalBit) {
            binStr = zero.substring(0, totalBit - binStr.length) + binStr
        }
        return binStr.toCharArray()
    }

    fun combineBit(hi4Bit: Int, low4Bit4: Int): Byte {
        return combineBit(toBitArray(hi4Bit, 4), toBitArray(low4Bit4, 4))
    }

    fun combineBit(hi4Bit: Char, low4Bit4: Char): Byte {
        return combineBit(hi4Bit.digitToInt(), low4Bit4.digitToInt())
    }

    /**
     * 将两个4字合并位一个字节
     */
    fun combineBit(hi4Bit: CharArray, low4Bit4: CharArray): Byte {
        return (String(hi4Bit) + String(low4Bit4)).toInt(2).toByte()
    }

    /**
     * 长整型转换为字节数组(小端模式，低位在前),最大支持8个字节
     * @param longValue
     */
    fun long2Byte(longValue: Long, totalBytes: Int): ByteArray {
        val b = ByteArray(totalBytes)
        b.forEachIndexed { index, _ ->
            b[index] =
                (longValue shr 8 * (totalBytes - 1 - index) and 0xFF).toByte()
        }
        return b.reversedArray()
    }

    /**
     * 整型转换为字节数组(小端模式，低位在前),最大支持8个字节
     * @param intValue
     */
    fun int2Byte(intValue: Int, totalBytes: Int): ByteArray {
        val b = ByteArray(totalBytes)
        b.forEachIndexed { index, _ ->
            b[index] =
                (intValue shr 8 * (totalBytes - 1 - index) and 0xFF).toByte()
        }
        return b.reversedArray()
    }

    /**
     * 字节数组转换为整型(小端模式，低位在前)
     */
    fun byte2Int(byteArray: ByteArray, totalBytes: Int): Int {
        var i = 0
        var tmp: Int
        byteArray.reverse()
        byteArray.forEachIndexed { index, _ ->
            tmp = byteArray[index].toInt() and 0xFF shl 8 * (totalBytes - 1 - index)
            if (index == 0) {
                i = tmp
                return@forEachIndexed
            }
            i = i or tmp
        }
        return i
    }

    /**
     * 字符串转换位字节数组
     */
    fun string2ByteArray(string: String): ByteArray {
        val list = mutableListOf<Byte>()
        string.replace("[", "")
            .replace("]", "")
            .trim()
            .split(",").forEach {
                list.add(it.trim().toInt(10).toByte())
            }
        return list.toByteArray()
    }
}