package com.silent.datautils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DataUtils {
    /**
     * 获取文件MD5值
     */
    fun getFileMD5(file: File): String {
        if (!file.isFile) {
            return ""
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
            return ""
        }
        return toHexString(digest.digest())
    }

    /**
     * 将字节数组转换为16进制的字符串
     */
    fun toHexString(src: ByteArray?): String {
        return Hex.toHexString(src)
    }

    /**
     * 将字符串转换为字节数组
     * 转换规则：010203->0x1,0x2,0x3
     */
    fun toByteArray(data: String): ByteArray {
        return Hex.decode(data)
    }

    object MODE {
        const val AES_CBC_Nothing = "AES/CBC/Nothing"
    }

    /**
     * 加密
     * @param data 明文
     * @param key  密钥
     */
    fun encrypt(data: ByteArray, key: ByteArray, mode: String, iv: String? = null): ByteArray {
        return try {
            Security.addProvider(BouncyCastleProvider())
            val secretKey = SecretKeySpec(key, mode)
            val cipher = Cipher.getInstance(mode) // 创建密码器
            if (null == iv) {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey
                ) // 初始化
            } else {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    IvParameterSpec(Hex.decode(iv))
                )// 初始化
            }
            cipher.doFinal(data) // 加密
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }

    /**
     * 解密
     * @param data 密文
     * @param key  密钥
     */
    fun decrypt(data: ByteArray, key: ByteArray, mode: String, iv: String? = null): ByteArray {
        return try {
            Security.addProvider(BouncyCastleProvider())
            val secretKey = SecretKeySpec(key, mode)
            val cipher = Cipher.getInstance(mode) // 创建密码器
            if (null == iv) {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey
                ) // 初始化
            } else {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    IvParameterSpec(Hex.decode(iv))
                )// 初始化
            }
            cipher.doFinal(data) // 解密
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }
}