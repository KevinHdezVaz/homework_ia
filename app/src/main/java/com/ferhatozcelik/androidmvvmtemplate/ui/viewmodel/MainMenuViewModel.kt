package com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MainMenuUiState>(MainMenuUiState.Initial)
    val uiState: StateFlow<MainMenuUiState> = _uiState

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processFileForOcr(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = MainMenuUiState.Loading
            try {
                val text = when {
                    isPdfFile(uri) -> processPdfFile(context, uri)
                    else -> processImageFile(context, uri)
                }
                _uiState.value = MainMenuUiState.Success(text)
            } catch (e: Exception) {
                _uiState.value = MainMenuUiState.Error("Error al procesar el archivo: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun processImageFile(context: Context, uri: Uri): String = withContext(Dispatchers.Default) {
        val image = InputImage.fromFilePath(context, uri)
        return@withContext recognizer.process(image).await().text
    }

    private suspend fun processPdfFile(context: Context, uri: Uri): String = withContext(Dispatchers.Default) {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor

        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val totalPages = pdfRenderer.pageCount
        val stringBuilder = StringBuilder()

        for (i in 0 until totalPages) {
            val page = pdfRenderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            val image = InputImage.fromBitmap(bitmap, 0)
            val pageText = recognizer.process(image).await().text
            stringBuilder.append(pageText).append("\n\n--- Página ${i + 1} ---\n\n")

            page.close()
            bitmap.recycle()
        }

        pdfRenderer.close()
        parcelFileDescriptor.close()

        return@withContext stringBuilder.toString()
    }

    private fun isPdfFile(uri: Uri): Boolean {
        return uri.path?.lowercase()?.endsWith(".pdf") ?: false
    }
}

sealed class MainMenuUiState {
    object Initial : MainMenuUiState()
    object Loading : MainMenuUiState()
    data class Success(val text: String) : MainMenuUiState()
    data class Error(val message: String) : MainMenuUiState()
}