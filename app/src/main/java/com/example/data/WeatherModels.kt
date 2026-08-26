package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "current") val current: CurrentWeather? = null,
    @Json(name = "daily") val daily: DailyWeather? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "time") val time: String? = null,
    @Json(name = "temperature_2m") val temperature2m: Double? = null,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Double? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "precipitation") val precipitation: Double? = null,
    @Json(name = "rain") val rain: Double? = null,
    @Json(name = "cloud_cover") val cloudCover: Double? = null,
    @Json(name = "pressure_msl") val pressureMsl: Double? = null,
    @Json(name = "surface_pressure") val surfacePressure: Double? = null,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_direction_10m") val windDirection10m: Double? = null,
    @Json(name = "wind_gusts_10m") val windGusts10m: Double? = null,
    @Json(name = "is_day") val isDay: Int? = null
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "time") val time: List<String>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int>? = null,
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double>? = null,
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double>? = null,
    @Json(name = "apparent_temperature_max") val apparentTemperatureMax: List<Double>? = null,
    @Json(name = "apparent_temperature_min") val apparentTemperatureMin: List<Double>? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null,
    @Json(name = "sunrise") val sunrise: List<String>? = null,
    @Json(name = "sunset") val sunset: List<String>? = null,
    @Json(name = "daylight_duration") val daylightDuration: List<Double>? = null,
    @Json(name = "moonrise") val moonrise: List<String>? = null,
    @Json(name = "moonset") val moonset: List<String>? = null,
    @Json(name = "moon_phase") val moonPhase: List<Double>? = null
)
