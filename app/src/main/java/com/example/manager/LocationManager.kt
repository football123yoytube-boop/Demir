package com.example.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val districtName: String? = null,
    val countryName: String? = null,
    val isPermissionGranted: Boolean = true
)

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    suspend fun getCurrentLocation(fallbackLat: Double = 41.0082, fallbackLng: Double = 28.9784): LocationData =
        withContext(Dispatchers.IO) {
            if (!hasLocationPermission()) {
                val resolvedName = resolveCityName(fallbackLat, fallbackLng)
                return@withContext LocationData(
                    latitude = fallbackLat,
                    longitude = fallbackLng,
                    cityName = resolvedName ?: "İstanbul",
                    countryName = "Türkiye",
                    isPermissionGranted = false
                )
            }

            try {
                val cts = CancellationTokenSource()
                val location = suspendCancellableCoroutine { cont ->
                    try {
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cts.token
                        ).addOnSuccessListener { loc ->
                            if (loc != null) {
                                cont.resume(loc)
                            } else {
                                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                                    cont.resume(lastLoc)
                                }.addOnFailureListener {
                                    cont.resume(null)
                                }
                            }
                        }.addOnFailureListener {
                            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                                cont.resume(lastLoc)
                            }.addOnFailureListener {
                                cont.resume(null)
                            }
                        }
                    } catch (e: SecurityException) {
                        cont.resume(null)
                    }
                }

                if (location != null) {
                    val city = resolveCityName(location.latitude, location.longitude)
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        cityName = city ?: "Konum Tespit Edildi",
                        countryName = "Türkiye",
                        isPermissionGranted = true
                    )
                } else {
                    val resolvedName = resolveCityName(fallbackLat, fallbackLng)
                    LocationData(
                        latitude = fallbackLat,
                        longitude = fallbackLng,
                        cityName = resolvedName ?: "İstanbul",
                        countryName = "Türkiye",
                        isPermissionGranted = true
                    )
                }
            } catch (e: Exception) {
                val resolvedName = resolveCityName(fallbackLat, fallbackLng)
                LocationData(
                    latitude = fallbackLat,
                    longitude = fallbackLng,
                    cityName = resolvedName ?: "İstanbul",
                    countryName = "Türkiye",
                    isPermissionGranted = hasLocationPermission()
                )
            }
        }

    private fun resolveCityName(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("tr", "TR"))
            @Suppress("DEPRECATION")
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val subAdmin = address.subAdminArea ?: address.locality ?: address.subLocality
                val admin = address.adminArea ?: address.countryName
                if (subAdmin != null && admin != null && !subAdmin.equals(admin, ignoreCase = true)) {
                    "$subAdmin, $admin"
                } else {
                    subAdmin ?: admin ?: address.featureName ?: "İstanbul"
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
