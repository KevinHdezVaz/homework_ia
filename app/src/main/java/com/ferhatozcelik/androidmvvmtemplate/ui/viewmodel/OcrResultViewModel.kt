package com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcrResultViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<OcrResultUiState>(OcrResultUiState.Initial)
    val uiState: StateFlow<OcrResultUiState> = _uiState

    fun setOcrText(text: String) {
        _uiState.value = OcrResultUiState.Content(text)
    }

    fun processWithAI(text: String) {
        viewModelScope.launch {
            _uiState.value = OcrResultUiState.Loading
            // Aquí iría la lógica para procesar el texto con IA
            // Por ahora, simplemente simularemos una respuesta después de un delay
            kotlinx.coroutines.delay(2000)
            _uiState.value = OcrResultUiState.AIProcessed("Respuesta simulada de la IA: $text")
        }
    }
}

sealed class OcrResultUiState {
    object Initial : OcrResultUiState()
    object Loading : OcrResultUiState()
    data class Content(val text: String) : OcrResultUiState()
    data class AIProcessed(val response: String) : OcrResultUiState()
    data class Error(val message: String) : OcrResultUiState()
}