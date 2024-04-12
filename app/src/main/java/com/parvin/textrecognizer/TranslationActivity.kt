package com.parvin.textrecognizer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.parvin.textrecognizer.databinding.ActivityTranslationBinding

class TranslationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTranslationBinding
    private lateinit var targetText: String
    private lateinit var sourceText: String
    private lateinit var options: TranslatorOptions
    private lateinit var translator: Translator
    private lateinit var selectedSourceLanguageCode: String
    private lateinit var selectedSourceLanguageName: String
    private lateinit var selectedTargetLanguageCode: String
    private lateinit var selectedTargetLanguageName: String

    //add require wifi later
    private val conditions = DownloadConditions.Builder().build()
    private val adapter by lazy { ArrayAdapter(this, R.layout.spinner_item, supportedLanguageNames) }

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

        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding.spinnerSourceLanguage.adapter = adapter
        binding.spinnerTargetLanguage.adapter = adapter

        sourceText = intent.getStringExtra("source_text") ?: return
        Toast.makeText(applicationContext, sourceText, Toast.LENGTH_SHORT).show()
        binding.textViewSourceText.text = sourceText

        binding.setUpListeners()

    }

    private fun ActivityTranslationBinding.setUpListeners(){
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTargetLanguageCode = "en"
                selectedTargetLanguageName = "English"
            }

        }

        buttonTranslate.setOnClickListener {
            translate(sourceText)
        }

    }

    private fun translate(text: String) {
        downloadRequiredModel(selectedSourceLanguageCode, selectedTargetLanguageCode)

        translator.translate(text)
            .addOnSuccessListener {
                targetText = it
                binding.textViewTargetText.text = targetText
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error: $it", Toast.LENGTH_SHORT).show()
            }
       // return targetText
    }

    private fun downloadRequiredModel(sourceCode: String, targetCode: String){
        options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceCode)?:return)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetCode)?:return)
            .build()
        translator = Translation.getClient(options)

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Model Downloaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error: $it", Toast.LENGTH_SHORT).show()
            }
    }

}