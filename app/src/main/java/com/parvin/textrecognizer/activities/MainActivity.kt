package com.parvin.textrecognizer.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.parvin.textrecognizer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            cropImage.launch(
                CropImageContractOptions(uri, cropImageOptions = CropImageOptions())
            )
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        result.uriContent?.let { uri ->
            startActivity(
                Intent(this, TextRecognitionActivity::class.java)
                    .putExtra("image_uri", uri.toString())
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setUpListeners()
    }

    private fun ActivityMainBinding.setUpListeners() {
        linearLayoutCamera.setOnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        linearLayoutGallery.setOnClickListener {
            imageContract.launch("image/*")
        }
    }

}