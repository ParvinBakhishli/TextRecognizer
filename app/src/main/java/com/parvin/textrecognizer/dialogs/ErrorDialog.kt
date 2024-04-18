package com.parvin.textrecognizer.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.parvin.textrecognizer.databinding.DialogErrorBinding

class ErrorDialog : DialogFragment() {

    private var _binding: DialogErrorBinding? = null
    val binding get() = requireNotNull(_binding)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogErrorBinding.inflate(inflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false


        binding.textViewErrorBody.text = arguments?.getString("message")

        binding.buttonOk.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setDimAmount(0.6f)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


    companion object {
        fun newInstance(message: String) = ErrorDialog().apply {
            arguments = bundleOf("message" to message)
        }

        const val TAG = "error_dialog"
    }

}
