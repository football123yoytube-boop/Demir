package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

sealed class WeatherResult {
    data class Success(val response: WeatherResponse, val isFromCache: Boolean = false) : WeatherResult()
    data class Error(val message: String, val cachedResponse: WeatherResponse? = null) : WeatherResult()
}

class WeatherRepository {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: WeatherApi = retrofit.create(WeatherApi::class.java)

    private var lastSuccessfulResponse: WeatherResponse? = null

    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherResult = withContext(Dispatchers.IO) {
        try {
            val response = api.getForecast(
                latitude = latitude,
                longitude = longitude
            )
            lastSuccessfulResponse = response
            WeatherResult.Success(response, isFromCache = false)
        } catch (e: Exception) {
            if (lastSuccessfulResponse != null) {
                WeatherResult.Success(lastSuccessfulResponse!!, isFromCache = true)
            } else {
                WeatherResult.Error(
                    message = e.localizedMessage ?: "Hava durumu verisi alınamadı",
                    cachedResponse = null
                )
            }
        }
    }

    fun getLastCachedData(): WeatherResponse? = lastSuccessfulResponse

    fun setCachedData(response: WeatherResponse) {
        lastSuccessfulResponse = response
    }
}
