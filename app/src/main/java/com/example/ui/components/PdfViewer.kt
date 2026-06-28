package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PdfViewer(file: File, modifier: Modifier = Modifier) {
    val bitmapList = remember { mutableStateListOf<Bitmap>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(file) {
        isLoading = true
        errorMessage = null
        bitmapList.clear()
        
        try {
            Log.d("PdfViewer", "Opening PDF file: ${file.absolutePath} (Size: ${file.length()} bytes)")
            
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            
            Log.d("PdfViewer", "PDF opened. Page count: ${renderer.pageCount}")
            
            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                // Adjust scale based on screen width or fixed value
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmapList.add(bitmap)
                page.close()
            }
            
            renderer.close()
            fd.close()
            Log.d("PdfViewer", "Successfully rendered all pages.")
        } catch (e: Exception) {
            Log.e("PdfViewer", "Failed to render PDF", e)
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Failed to display PDF: $errorMessage", color = Color.Red)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize().background(Color.LightGray),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(bitmapList) { _, bitmap ->
                Card(
                    modifier = Modifier.fillMaxWidth().aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat()),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "PDF Page",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
