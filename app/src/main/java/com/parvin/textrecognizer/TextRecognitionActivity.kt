package com.parvin.textrecognizer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.parvin.textrecognizer.databinding.ActivityTextRecognitionBinding
import java.io.IOException

class TextRecognitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextRecognitionBinding
    private lateinit var uri : Uri
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var detectedText: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uri = intent.getStringExtra("image_uri")?.toUri() ?: return
        binding.imageViewImage.setImageURI(uri)

        binding.setUpListeners()

    }

    private fun ActivityTextRecognitionBinding.setUpListeners(){
        buttonDetectText.setOnClickListener {
            detectTextFromImage()

        }

        textViewCopy.setOnClickListener {
        }

        textViewTranslate.setOnClickListener {
            val intent = Intent(this@TextRecognitionActivity, TranslationActivity::class.java)
            intent.putExtra("source_text", detectedText)
            startActivity(intent)
        }
    }



    private fun detectTextFromImage(): String{
        val image = InputImage.fromBitmap(getBitmapFromUri(uri) ?: return "",0)
        detectedText = "Parvin"

        recognizer.process(image).addOnSuccessListener { visionText ->
            detectedText = visionText.text
            binding.textViewDetectedText.text = detectedText
            Toast.makeText(applicationContext,"Success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            binding.textViewDetectedText.text = "Text recognition failed: ${it.message}"
            Toast.makeText(applicationContext,"Failed", Toast.LENGTH_SHORT).show()
        }
        return detectedText
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}