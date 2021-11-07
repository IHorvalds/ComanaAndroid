package com.example.comana

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

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

private fun randomUsername(): String {
    // Positive adjectives. 44 of them.
    val adjectives: Array<String> = arrayOf(
        "Adaptable",
        "Adventurous",
        "Amarous",
        "diligent",
        "Humble",
        "Courageous",
        "Efficient",
        "Enchanting",
        "Generous",
        "Magnetic",
        "Likeable",
        "Sincere",
        "Trustworthy",
        "Resourceful",
        "Wise",
        "Resilient",
        "Reliable",
        "Determined",
        "Strong",
        "Stupendous",
        "Exceptional",
        "Generous",
        "Kind",
        "Persuasive",
        "Vivacious",
        "Witty",
        "Extraordinary",
        "Divine",
        "Breathtaking",
        "Flawless",
        "Magnificent",
        "Lively",
        "Versatile",
        "Amazing",
        "Outgoing",
        "amicable",
        "Friendly",
        "Perseverant",
        "Enthusiastic",
        "Affectionate",
        "Thoughtful",
        "Modest",
        "Hygienic",
        "Considerate",
        "Courteous"
    )

    // Cute animals. 44 of them
    val animals: Array<String> = arrayOf(
        "Red Panda",
        "Quokka",
        "Panda",
        "Dog",
        "Fennec Fox",
        "Rabbit",
        "Koala",
        "Otter",
        "Meerkat",
        "Hedgehog",
        "Raccoon",
        "Flamingo",
        "Cat",
        "Lion",
        "Hamster",
        "Parrot",
        "Chameleon",
        "Tiger",
        "Turtle",
        "Squirrel",
        "Frog",
        "Bear",
        "Pangolin",
        "Armadillo",
        "Beaver",
        "Xerus",
        "Fossa",
        "Cuttlefish",
        "Narwhal",
        "Lynx",
        "Pika",
        "Klipspringer",
        "Margay",
        "Capybara",
        "Quoll",
        "Bongo",
        "Axolotl",
        "Kangaroo",
        "Bearded Tamarin",
        "Dolphin",
        "Owl",
        "Wolf",
        "Sea Lion",
        "Deer",
        "Walrus"
    )
    return adjectives.random() + " " + animals.random()
}

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

        shared = getSharedPreferences("com.example.comana.UserUUID" , Context.MODE_PRIVATE)
        val uuid = shared.getString("uuid", "")
        val username = shared.getString("username", "")
        Log.v("mylog","[before] uuid=" + uuid)
        if (uuid == "") {
            val editor = shared.edit()
            editor.putString("uuid", randomID())
            editor.commit()
        }
        if (username == "") {
            val editor = shared.edit()
            editor.putString("username", randomUsername())
            editor.commit()
        }
        var check_uuid = shared.getString("uuid", "")
        assert(check_uuid != "")
        println("[after] uuid=" + check_uuid)

        val uuidTextView = findViewById<TextView>(R.id.userid_textview)
        uuidTextView.text = check_uuid
        val usernameTextView = findViewById<TextView>(R.id.username_textview)
        usernameTextView.text = shared.getString("username", "")

        val newGroupButton = findViewById<Button>(R.id.new_group_button)
        newGroupButton.setOnClickListener {
            val intent = Intent(this@MainActivity, BikeRideActivity::class.java)

            // TODO: put a loader: inform that the group is being created.

            // Generate the group key for symmetric encryption.
            val publicGroupKey = randomID()

            // Set the key.
            AESKnowledgeFactory.setKey(publicGroupKey)

            // And start the activity.
            startActivity(intent)
        }
    }
}