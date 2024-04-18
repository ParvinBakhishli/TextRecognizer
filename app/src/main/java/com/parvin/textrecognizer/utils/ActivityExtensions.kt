package com.parvin.textrecognizer.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.parvin.textrecognizer.dialogs.ErrorDialog


fun AppCompatActivity.showError(message: String?) {
    if (supportFragmentManager.findFragmentByTag(ErrorDialog.TAG) != null) {
        return
    }

    ErrorDialog.newInstance(message ?: "Unexpected error occurred.")
        .show(supportFragmentManager, ErrorDialog.TAG)
}

fun AppCompatActivity.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

infix fun ViewBinding.snack(msg: String) {
    Snackbar.make(root, msg, Snackbar.LENGTH_SHORT).show()
}