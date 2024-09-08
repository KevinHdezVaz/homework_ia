package com.ferhatozcelik.androidmvvmtemplate.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ferhatozcelik.androidmvvmtemplate.databinding.ActivityMainBinding
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.MainMenuUiState
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.MainMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainMenuViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.processFileForOcr(this, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeUiState()
    }

    private fun setupUI() {
        binding.btnUploadTask.setOnClickListener {
            getContent.launch("*/*")
        }

    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MainMenuUiState.Loading -> {
                        // Mostrar indicador de carga
                        Toast.makeText(this@MainActivity, "Procesando archivo...", Toast.LENGTH_SHORT).show()
                    }
                    is MainMenuUiState.Success -> {
                        val intent = Intent(this@MainActivity, OcrResultActivity::class.java)
                        intent.putExtra("OCR_TEXT", state.text)
                        startActivity(intent)
                    }
                    is MainMenuUiState.Error -> {
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {} // Manejar otros estados si es necesario
                }
            }
        }
    }
}