package com.example.comana

import android.R.attr
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
/* QR Code */
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import android.R.attr.label
import android.content.*
import android.widget.*


class NewGroupActivity : AppCompatActivity() {

    lateinit var shared : SharedPreferences
    private lateinit var startButton: Button
    private lateinit var copyButton: ImageButton
    private lateinit var editText: EditText
    private lateinit var imageView: ImageView

    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) { Log.d("MainActivity", "generateQRCode: ${e.message}") }
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgroup)

        shared = getSharedPreferences("com.example.comana.UserUUID" , Context.MODE_PRIVATE)
        val uuidTextView = findViewById<TextView>(R.id.userid_textview)
        uuidTextView.text = shared.getString("uuid", "")
        val usernameTextView = findViewById<TextView>(R.id.username_textview)
        usernameTextView.text = shared.getString("username", "")

        val actionBar = supportActionBar
        actionBar!!.title = "New Group Activity"
        actionBar.setDisplayHomeAsUpEnabled(true)

        startButton = findViewById(R.id.qr_button)
        copyButton = findViewById(R.id.copy_button)
        editText = findViewById(R.id.qr_editText)
        imageView = findViewById(R.id.qr_imageView)

        // Generate the group key for symmetric encryption.
        val publicGroupKey = randomID()

        // Set the key.
        AESKnowledgeFactory.setKey(publicGroupKey)

        val groupId = randomID()
        val editor = shared.edit()
        editor.putString("group_id", groupId)
        editor.commit()
        editText.setText(groupId)
        val bitmap = generateQRCode(groupId)
        imageView.setImageBitmap(bitmap)

        startButton.setOnClickListener {
            val intent = Intent(this, BikeRideActivity::class.java)
            startActivity(intent)
        }

        copyButton.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Group ID", groupId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}