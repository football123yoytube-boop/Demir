package com.example.manager

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class StopwatchState(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Long> = emptyList()
)

data class CountdownState(
    val totalSeconds: Int = 300, // 5 min default
    val remainingSeconds: Int = 300,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

data class AlarmState(
    val hour: Int = 8,
    val minute: Int = 0,
    val isEnabled: Boolean = false,
    val label: String = "Sabah Uyanış"
)

class TimerManager(private val context: Context, private val scope: CoroutineScope) {

    private val _stopwatch = MutableStateFlow(StopwatchState())
    val stopwatch: StateFlow<StopwatchState> = _stopwatch.asStateFlow()

    private val _countdown = MutableStateFlow(CountdownState())
    val countdown: StateFlow<CountdownState> = _countdown.asStateFlow()

    private val _alarm = MutableStateFlow(AlarmState())
    val alarm: StateFlow<AlarmState> = _alarm.asStateFlow()

    private var stopwatchJob: Job? = null
    private var countdownJob: Job? = null

    fun startStopwatch() {
        if (_stopwatch.value.isRunning) return
        _stopwatch.value = _stopwatch.value.copy(isRunning = true)
        stopwatchJob = scope.launch(Dispatchers.Default) {
            val startTime = System.currentTimeMillis() - _stopwatch.value.elapsedMillis
            while (isActive && _stopwatch.value.isRunning) {
                val current = System.currentTimeMillis() - startTime
                _stopwatch.value = _stopwatch.value.copy(elapsedMillis = current)
                delay(30)
            }
        }
    }

    fun pauseStopwatch() {
        stopwatchJob?.cancel()
        _stopwatch.value = _stopwatch.value.copy(isRunning = false)
    }

    fun resetStopwatch() {
        stopwatchJob?.cancel()
        _stopwatch.value = StopwatchState()
    }

    fun lapStopwatch() {
        if (_stopwatch.value.elapsedMillis > 0) {
            val updatedLaps = _stopwatch.value.laps + _stopwatch.value.elapsedMillis
            _stopwatch.value = _stopwatch.value.copy(laps = updatedLaps)
        }
    }

    fun setCountdownDuration(seconds: Int) {
        countdownJob?.cancel()
        _countdown.value = CountdownState(
            totalSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            isFinished = false
        )
    }

    fun startCountdown() {
        if (_countdown.value.isRunning || _countdown.value.remainingSeconds <= 0) return
        _countdown.value = _countdown.value.copy(isRunning = true, isFinished = false)
        countdownJob = scope.launch(Dispatchers.Default) {
            while (isActive && _countdown.value.remainingSeconds > 0) {
                delay(1000)
                val next = _countdown.value.remainingSeconds - 1
                _countdown.value = _countdown.value.copy(remainingSeconds = next)
                if (next <= 0) {
                    _countdown.value = _countdown.value.copy(isRunning = false, isFinished = true)
                    triggerAlarmHaptic()
                    break
                }
            }
        }
    }

    fun pauseCountdown() {
        countdownJob?.cancel()
        _countdown.value = _countdown.value.copy(isRunning = false)
    }

    fun resetCountdown() {
        countdownJob?.cancel()
        val total = _countdown.value.totalSeconds
        _countdown.value = CountdownState(
            totalSeconds = total,
            remainingSeconds = total,
            isRunning = false,
            isFinished = false
        )
    }

    fun setAlarm(hour: Int, minute: Int, enabled: Boolean, label: String = "Alarm") {
        _alarm.value = AlarmState(hour, minute, enabled, label)
    }

    fun toggleAlarm() {
        _alarm.value = _alarm.value.copy(isEnabled = !_alarm.value.isEnabled)
    }

    fun triggerHaptic(strong: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = if (strong) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vm?.defaultVibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (strong) 50L else 20L)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun triggerAlarmHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val pattern = longArrayOf(0, 300, 200, 300, 200, 500)
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vm?.defaultVibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val pattern = longArrayOf(0, 300, 200, 300, 200, 500)
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
