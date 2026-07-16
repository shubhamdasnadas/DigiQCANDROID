package com.example.data

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class ChecklistItem(
    val name: String,
    val category: String? = null
)

@JsonClass(generateAdapter = true)
data class OrganizationItem(
    val name: String,
    val code: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String,
    val organization: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val username: String?,
    val organization: String?
)

interface CisoBackendApi {
    @GET("api/checklists")
    suspend fun getChecklists(): List<ChecklistItem>

    @GET("api/organizations")
    suspend fun getOrganizations(): List<OrganizationItem>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    companion object {
        private var currentBaseUrl = "http://10.0.2.2:3000/" // Default loopback to host port 3000 in Android emulator

        fun updateBaseUrl(newUrl: String) {
            currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
            retrofitInstance = null
        }

        fun getBaseUrl(): String = currentBaseUrl

        private var retrofitInstance: CisoBackendApi? = null

        fun getInstance(): CisoBackendApi {
            if (retrofitInstance == null) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build()

                retrofitInstance = Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(CisoBackendApi::class.java)
            }
            return retrofitInstance!!
        }
    }
}
