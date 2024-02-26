
package com.finogeeks.finclip.utils



import com.blankj.utilcode.util.ConvertUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object DES {
    private const val DES_ALGORITHM = "DES/ECB/PKCS7Padding"
    private val BC_PROVIDER: Provider = BouncyCastleProvider()
    fun encrypt(plainText: String, key: String): String {
        Security.addProvider(BC_PROVIDER)
        val keySpec = SecretKeySpec(key.toByteArray(), "DES")
        val cipher: Cipher = Cipher.getInstance(DES_ALGORITHM, BC_PROVIDER)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedBytes: ByteArray = cipher.doFinal(plainText.toByteArray())
        return ConvertUtils.bytes2HexString(encryptedBytes).lowercase()
    }

    fun decrypt(cipherText: String?, key: String): String{
        Security.addProvider(BC_PROVIDER)
        val keySpec = SecretKeySpec(key.toByteArray(), "DES")
        val cipher: Cipher = Cipher.getInstance(DES_ALGORITHM, BC_PROVIDER)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decryptedBytes: ByteArray = cipher.doFinal(ConvertUtils.hexString2Bytes(cipherText))
        return String(decryptedBytes)
    }
}
