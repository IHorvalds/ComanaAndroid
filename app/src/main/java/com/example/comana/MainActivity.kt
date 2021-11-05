package com.example.comana

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

/* Cryptography */
import javax.crypto.Cipher
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.util.*

// Inspired from: https://www.codegrepper.com/code-examples/kotlin/kotlin+generate+a+random+key
private fun randomID(): String = List(16) {
    (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
}.joinToString("")

// Inspired from: https://www.knowledgefactory.net/2021/01/kotlin-aes-rsa-3des-encryption-and.html
object AESKnowledgeFactory {
    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray

    // Set Key
    fun setKey(myKey: String) {
        // TODO: is this a persistent?
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // Encrypt the text.
    fun encrypt(strToEncrypt: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    // Decrypt the text.
    fun decrypt(strToDecrypt: String?): String? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }
}

class MainActivity : AppCompatActivity() {
    lateinit var shared : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shared = getSharedPreferences("User" , Context.MODE_PRIVATE)
        var uuid = shared.getString("uuid", "")
        println("[before] uuid=" + uuid)
        if (uuid == "") {
            val editor = shared.edit()
            editor.putString("uuid", randomID())
            editor.commit()
        }
        var check_uuid = shared.getString("uuid", "")
        assert(check_uuid != "")
        println("[after] uuid=" + check_uuid)

        val newGroupButton = findViewById<Button>(R.id.new_group_button)
        newGroupButton.setOnClickListener {
            val intent = Intent(this@MainActivity, BikeRideActivity::class.java)

            // TODO: put a loader: inform that the group is being created.

            // Generate the group key for symmetric encryption.
            val groupKey = randomID()

            // Set the key.
            AESKnowledgeFactory.setKey(groupKey)

            // And start the activity.
            startActivity(intent)
        }
    }
}