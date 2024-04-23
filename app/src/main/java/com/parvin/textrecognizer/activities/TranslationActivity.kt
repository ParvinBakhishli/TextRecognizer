package com.parvin.textrecognizer.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.parvin.textrecognizer.databinding.ActivityTranslationBinding
import com.parvin.textrecognizer.utils.shortToast
import com.parvin.textrecognizer.utils.showError

class TranslationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTranslationBinding
    private lateinit var targetText: String
    private lateinit var sourceText: String
    private lateinit var translator: Translator
    private lateinit var selectedSourceLanguageCode: String
    private lateinit var selectedSourceLanguageName: String
    private lateinit var selectedTargetLanguageCode: String
    private lateinit var selectedTargetLanguageName: String

    private val adapter by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            supportedLanguageNames
        )
    }

    private val languageMap = mapOf(
        "en" to "English",
        "ar" to "Arabic",
        "bg" to "Bulgarian",
        "cs" to "Czech",
        "de" to "German",
        "el" to "Greek",
        "es" to "Spanish",
        "fa" to "Persian",
        "fr" to "French",
        "ga" to "Irish",
        "hi" to "Hindi",
        "hr" to "Croatian",
        "hu" to "Hungarian",
        "it" to "Italian",
        "ja" to "Japanese",
        "ka" to "Georgian",
        "ko" to "Korean",
        "nl" to "Dutch",
        "pl" to "Polish",
        "pt" to "Portuguese",
        "ro" to "Romanian",
        "ru" to "Russian",
        "tr" to "Turkish",
        "zh" to "Chinese"
    )
    private val supportedLanguageCodes = languageMap.entries.sortedBy { it.value }.map { it.key }
    private val supportedLanguageNames = languageMap.entries.sortedBy { it.value }.map { it.value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        adapter.setDropDownViewResource(R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        binding.spinnerSourceLanguage.adapter = adapter
        binding.spinnerTargetLanguage.adapter = adapter

        sourceText = intent.getStringExtra("source_text") ?: return
        Toast.makeText(applicationContext, sourceText, Toast.LENGTH_SHORT).show()
        binding.textViewSourceText.text = sourceText

        binding.setUpListeners()

    }

    private fun ActivityTranslationBinding.setUpListeners() {

        spinnerSourceLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSourceLanguageCode = supportedLanguageCodes[position]
                selectedSourceLanguageName = languageMap[selectedSourceLanguageCode] ?: return

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSourceLanguageCode = "en"
                selectedSourceLanguageName = "English"
            }

        }

        spinnerTargetLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTargetLanguageCode = supportedLanguageCodes[position]
                selectedTargetLanguageName = languageMap[selectedTargetLanguageCode] ?: return

                translate(sourceText)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTargetLanguageCode = "en"
                selectedTargetLanguageName = "English"
            }

        }

        imageViewCopy.setOnClickListener {
            copyToClipBoard()
        }

    }

    private fun translate(text: String) {
        binding.progressIndicator.isVisible = true

        downloadRequiredModel(selectedSourceLanguageCode, selectedTargetLanguageCode) {
            translator.translate(text)
                .addOnSuccessListener {
                    targetText = it
                    binding.textViewTargetText.text = targetText
                }
                .addOnFailureListener {
                    showError(it.message ?: "Unexpected Error")
                }
                .addOnCompleteListener {
                    binding.progressIndicator.isVisible = false

                    /*deleteModel(selectedSourceLanguageCode) {
                        shortToast("$selectedSourceLanguageCode is deleted")
                    }
                    deleteModel(selectedTargetLanguageCode) {
                        shortToast("$selectedTargetLanguageCode is deleted")
                    }*/
                }
        }
    }

    private fun downloadRequiredModel(
        sourceCode: String, targetCode: String,
        onModelAvailable: () -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceCode) ?: return)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetCode) ?: return)
            .build()

        translator = Translation.getClient(options)
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                onModelAvailable()
            }
            .addOnFailureListener {
                showError(it.message)
            }
    }

    private fun deleteModel(code: String, onSuccess: () -> Unit = {}) {
        RemoteModelManager.getInstance()
            .deleteDownloadedModel(TranslateRemoteModel.Builder(code).build())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { showError(it.message) }
    }

    private fun copyToClipBoard() {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText("text",targetText)
        clipboardManager.setPrimaryClip(clipData)
        shortToast("Copied to Clipboard")
    }

    override fun onDestroy() {
        translator.close()
        super.onDestroy()
    }

}