package com.ferhatozcelik.androidmvvmtemplate.ui.activitys

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
 import com.ferhatozcelik.androidmvvmtemplate.databinding.ActivityOcrResultBinding
 import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.OcrResultUiState
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.OcrResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OcrResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOcrResultBinding
    private val viewModel: OcrResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ocrText = intent.getStringExtra("OCR_TEXT") ?: ""
        viewModel.setOcrText(ocrText)

        setupUI()
        observeUiState()
    }

    private fun setupUI() {
        binding.btnProcessWithAI.setOnClickListener {
            val editedText = binding.etOcrResult.text.toString()
            viewModel.processWithAI(editedText)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is OcrResultUiState.Content -> {
                        binding.etOcrResult.setText(state.text)
                    }
                    is OcrResultUiState.Loading -> {
                        binding.btnProcessWithAI.isEnabled = false
                        binding.btnProcessWithAI.text = "Procesando..."
                    }
                    is OcrResultUiState.AIProcessed -> {
                        binding.btnProcessWithAI.isEnabled = true
                        binding.btnProcessWithAI.text = "Procesar con IA"
                        Toast.makeText(this@OcrResultActivity, state.response, Toast.LENGTH_LONG).show()
                    }
                    is OcrResultUiState.Error -> {
                        binding.btnProcessWithAI.isEnabled = true
                        Toast.makeText(this@OcrResultActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }
}