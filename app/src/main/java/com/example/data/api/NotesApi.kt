package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class BackendNote(
    val id: String,
    val title: String,
    val description: String?,
    val subject: String,
    val pdfPath: String?,
    val thumbnailPath: String?,
    val adViewCount: Int?,
    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class AdViewRequest(
    val noteId: String,
    val viewerFingerprint: String
)

@JsonClass(generateAdapter = true)
data class TokenResponse(
    val accessToken: String,
    val expiresAt: String,
    val noteId: String
)

@JsonClass(generateAdapter = true)
data class NoteDetailResponse(
    val id: String,
    val title: String,
    val pdfUrl: String,
    val description: String?
)

interface NotesService {
    @GET("api/notes")
    suspend fun getNotes(): List<BackendNote>

    @POST("api/ad-views")
    suspend fun registerAdView(@Body body: AdViewRequest): TokenResponse

    @GET("api/notes/{id}")
    suspend fun getNoteDetails(
        @Path("id") id: String,
        @Header("x-access-token") token: String
    ): NoteDetailResponse
}

object NotesApiClient {
    private const val BASE_URL = "https://wbaatz-notes-backend.onrender.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val service: NotesService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NotesService::class.java)
    }
}
