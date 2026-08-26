package com.example.manager

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale

data class DeviceTelemetry(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val batteryTempCelsius: Float = 28.5f,
    val batteryHealth: String = "İyi",
    val batteryVoltageMv: Int = 4100,
    val isOnline: Boolean = true,
    val networkType: String = "Wi-Fi",
    val usedRamGb: Float = 4.2f,
    val totalRamGb: Float = 8.0f,
    val ramUsagePercent: Int = 52,
    val freeStorageGb: Float = 128.0f,
    val totalStorageGb: Float = 256.0f,
    val deviceModel: String = "Android Cihaz",
    val androidVersion: String = "Android 15",
    val compassDegrees: Float = 0f,
    val compassDirection: String = "Kuzey",
    val tiltPitch: Float = 0f,
    val tiltRoll: Float = 0f
)

class DeviceInfoManager(private val context: Context) : SensorEventListener {

    private val _telemetry = MutableStateFlow(DeviceTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { updateBattery(it) }
        }
    }

    fun startListening() {
        // Battery receiver
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)

        // Sensors
        if (rotationSensor != null) {
            sensorManager?.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            if (accelerometer != null) {
                sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            }
            if (magnetometer != null) {
                sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
            }
        }

        refreshStaticAndNetworkInfo()
    }

    fun stopListening() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // ignore if not registered
        }
        sensorManager?.unregisterListener(this)
    }

    fun refreshStaticAndNetworkInfo() {
        val (usedRam, totalRam, ramPercent) = getRamInfo()
        val (freeStorage, totalStorage) = getStorageInfo()
        val (isOnline, netType) = getNetworkInfo()
        val devModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        val osVer = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        _telemetry.value = _telemetry.value.copy(
            isOnline = isOnline,
            networkType = netType,
            usedRamGb = usedRam,
            totalRamGb = totalRam,
            ramUsagePercent = ramPercent,
            freeStorageGb = freeStorage,
            totalStorageGb = totalStorage,
            deviceModel = devModel,
            androidVersion = osVer
        )
    }

    private fun updateBattery(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else 100

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

        val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val healthStr = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "İyi"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Aşırı Isınma"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Tükenmiş"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Yüksek Voltaj"
            else -> "Normal"
        }

        _telemetry.value = _telemetry.value.copy(
            batteryPercent = percent,
            isCharging = isCharging,
            batteryTempCelsius = if (temp > 0) temp else 28.0f,
            batteryHealth = healthStr,
            batteryVoltageMv = voltage
        )
    }

    private fun getRamInfo(): Triple<Float, Float, Int> {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            val total = memInfo.totalMem / (1024f * 1024f * 1024f)
            val avail = memInfo.availMem / (1024f * 1024f * 1024f)
            val used = (total - avail).coerceAtLeast(0f)
            val percent = if (total > 0) ((used / total) * 100).toInt() else 50
            Triple(
                String.format(Locale.US, "%.1f", used).toFloat(),
                String.format(Locale.US, "%.1f", total).toFloat(),
                percent
            )
        } catch (e: Exception) {
            Triple(3.5f, 6.0f, 58)
        }
    }

    private fun getStorageInfo(): Pair<Float, Float> {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalGb = (totalBlocks * blockSize) / (1024f * 1024f * 1024f)
            val freeGb = (availableBlocks * blockSize) / (1024f * 1024f * 1024f)
            Pair(
                String.format(Locale.US, "%.1f", freeGb).toFloat(),
                String.format(Locale.US, "%.1f", totalGb).toFloat()
            )
        } catch (e: Exception) {
            Pair(64.0f, 128.0f)
        }
    }

    private fun getNetworkInfo(): Pair<Boolean, String> {
        return try {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = connManager?.activeNetwork
            val capabilities = connManager?.getNetworkCapabilities(activeNetwork)
            if (capabilities == null) {
                return Pair(false, "Çevrimdışı")
            }
            val isOnline = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val netType = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobil Veri (5G/4G)"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Bağlı"
            }
            Pair(isOnline, netType)
        } catch (e: Exception) {
            Pair(true, "Wi-Fi")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                if (azimuthDeg < 0) azimuthDeg += 360f

                val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

                val dir = getCompassDirection(azimuthDeg)
                _telemetry.value = _telemetry.value.copy(
                    compassDegrees = azimuthDeg,
                    compassDirection = dir,
                    tiltPitch = pitch,
                    tiltRoll = roll
                )
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, 3)
                hasGravity = true
                calculateOrientation()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagneticValues, 0, 3)
                hasGeomagnetic = true
                calculateOrientation()
            }
        }
    }

    private fun calculateOrientation() {
        if (hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                if (azimuthDeg < 0) azimuthDeg += 360f
                val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()
                val dir = getCompassDirection(azimuthDeg)
                _telemetry.value = _telemetry.value.copy(
                    compassDegrees = azimuthDeg,
                    compassDirection = dir,
                    tiltPitch = pitch,
                    tiltRoll = roll
                )
            }
        }
    }

    private fun getCompassDirection(degrees: Float): String {
        return when (degrees) {
            in 337.5..360.0, in 0.0..22.5 -> "Kuzey (N)"
            in 22.5..67.5 -> "Kuzeydoğu (NE)"
            in 67.5..112.5 -> "Doğu (E)"
            in 112.5..157.5 -> "Güneydoğu (SE)"
            in 157.5..202.5 -> "Güney (S)"
            in 202.5..247.5 -> "Güneybatı (SW)"
            in 247.5..292.5 -> "Batı (W)"
            in 292.5..337.5 -> "Kuzeybatı (NW)"
            else -> "Kuzey"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
