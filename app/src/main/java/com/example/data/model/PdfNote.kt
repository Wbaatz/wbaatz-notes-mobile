package com.example.data.model

data class PdfNote(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val pages: Int,
    val pdfUrl: String,
    val youtubeUrl: String,
    val duration: String,
    val contentPages: List<NotePage>,
    val isPremium: Boolean = false,
    val isLiveApi: Boolean = false
)

data class NotePage(
    val pageNumber: Int,
    val title: String,
    val body: String,
    val codeSnippet: String? = null
)
