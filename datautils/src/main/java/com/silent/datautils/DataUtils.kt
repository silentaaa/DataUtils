package com.silent.datautils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAPublicKeySpec
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

    /**
     * 加密
     * @param data 明文
     * @param key  密钥
     * @param mode  模式
     * @param iv  偏移量
     */
    fun encrypt(data: ByteArray, key: Key, mode: String, iv: String? = null): ByteArray {
        return try {
            Security.addProvider(BouncyCastleProvider())
            val cipher = Cipher.getInstance(mode) // 创建密码器
            if (null == iv) {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key
                ) // 初始化
            } else {
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    IvParameterSpec(Hex.decode(iv))
                )// 初始化
            }
            val blockSize = cipher.blockSize
            var plaintextLength = data.size
            if (plaintextLength % blockSize != 0) {
                plaintextLength += (blockSize - plaintextLength % blockSize)
            }
            val plaintext = ByteArray(plaintextLength)
            if (plaintextLength > data.size) plaintext[data.size] = 0x80.toByte() //填充数据为0x80后加0x00
            System.arraycopy(data, 0, plaintext, 0, data.size)
            cipher.doFinal(plaintext) // 加密
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }

    /**
     * 加密
     * @param data 明文
     * @param key  密钥
     * @param mode  模式
     * @param iv  偏移量
     */
    fun encrypt(data: ByteArray, key: ByteArray, mode: String, iv: String? = null): ByteArray {
        return encrypt(data, SecretKeySpec(key, mode), mode, iv)
    }

    /**
     * 加密
     * @param data 明文
     * @param modulus    n
     * @param publicExponent   e
     * @param mode  模式
     * @param iv  偏移量
     */
    fun encrypt(
        data: ByteArray,
        modulus: ByteArray,
        publicExponent: ByteArray,
        mode: String,
        iv: String? = null
    ): ByteArray {
        return encrypt(data, getPublicKey(modulus, publicExponent), mode, iv)
    }

    /**
     * 解密
     * @param data 密文
     * @param key  密钥
     * @param mode  模式
     * @param iv  偏移量
     */
    fun decrypt(data: ByteArray, key: ByteArray, mode: String, iv: String? = null): ByteArray {
        return try {
            Security.addProvider(BouncyCastleProvider())
            val secretKey = SecretKeySpec(key, mode)
            val cipher = Cipher.getInstance(mode) // 创建密码器
            if (null == iv) {
                cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey
                ) // 初始化
            } else {
                cipher.init(
                    Cipher.DECRYPT_MODE,
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

    /**
     * 使用n、e值还原公钥
     * @param modulus    n
     * @param publicExponent   e
     */
    private fun getPublicKey(modulus: ByteArray, publicExponent: ByteArray): PublicKey {
        val bigIntModulus = BigInteger(1, modulus)
        val bigIntPrivateExponent = BigInteger(1, publicExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
}