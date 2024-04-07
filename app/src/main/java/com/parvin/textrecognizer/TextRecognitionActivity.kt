package com.parvin.textrecognizer

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
        }
    }

    private fun detectTextFromImage(){
        val image = InputImage.fromBitmap(getBitmapFromUri(uri ?: return) ?: return,0)

        recognizer.process(image).addOnSuccessListener { visionText ->
            val resultText = visionText.text
            binding.textViewDetectedText.text = resultText
            Toast.makeText(applicationContext,"Success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            binding.textViewDetectedText.text = "Text recognition failed: ${it.message}"
            Toast.makeText(applicationContext,"Failed", Toast.LENGTH_SHORT).show()

        }
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}