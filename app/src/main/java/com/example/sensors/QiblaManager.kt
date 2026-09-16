package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class QiblaState(
    val azimuth: Float = 0f,             // Current device heading (degrees from North: 0..360)
    val qiblaBearing: Float = 278f,       // Qibla direction from current location
    val relativeAngle: Float = 0f,        // Angle of needle relative to top of phone
    val isFacingQibla: Boolean = false,   // Within +/- 3 degrees of Kaaba
    val accuracyStatus: String = "স্বাভাবিক",
    val hasSensor: Boolean = true
)

class QiblaManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _state = MutableStateFlow(QiblaState())
    val state: StateFlow<QiblaState> = _state.asStateFlow()

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var currentLat: Double = 23.8103 // Default Dhaka
    private var currentLng: Double = 90.4125

    init {
        updateLocation(currentLat, currentLng)
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        currentLat = latitude
        currentLng = longitude
        val qibla = calculateQiblaBearing(latitude, longitude)
        _state.value = _state.value.copy(qiblaBearing = qibla)
        updateRelativeAngle(_state.value.azimuth)
    }

    fun startListening() {
        val hasRot = rotationSensor != null
        val hasFallback = accelerometer != null && magnetometer != null

        if (!hasRot && !hasFallback) {
            _state.value = _state.value.copy(hasSensor = false)
            return
        }

        if (hasRot) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        var azimuthDegree = _state.value.azimuth

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            val rawAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuthDegree = (rawAzimuth + 360f) % 360f
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, 3)
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
            hasGeomagnetic = true
        }

        if (hasGravity && hasGeomagnetic && event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val rawAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuthDegree = (rawAzimuth + 360f) % 360f
            }
        }

        // Low-pass filter for smooth dial movement
        val smoothedAzimuth = smoothAngle(_state.value.azimuth, azimuthDegree)
        updateRelativeAngle(smoothedAzimuth)
    }

    private fun smoothAngle(current: Float, target: Float): Float {
        var diff = target - current
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return (current + diff * 0.2f + 360f) % 360f
    }

    private fun updateRelativeAngle(azimuth: Float) {
        val qibla = _state.value.qiblaBearing
        var relative = (qibla - azimuth + 360f) % 360f
        // Difference from perfect alignment:
        var diff = abs(qibla - azimuth)
        if (diff > 180f) diff = 360f - diff
        val isAligned = diff <= 4.0f

        _state.value = _state.value.copy(
            azimuth = azimuth,
            relativeAngle = relative,
            isFacingQibla = isAligned
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val status = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "উচ্চ নির্ভুলতা"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "মাঝারি"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "কম (ফোনটি ৮ আকৃতিতে ঘোরান)"
            else -> "ক্যালিব্রেট প্রয়োজন"
        }
        _state.value = _state.value.copy(accuracyStatus = status)
    }

    companion object {
        fun calculateQiblaBearing(lat: Double, lng: Double): Float {
            // Kaaba coordinates
            val kaabaLat = Math.toRadians(21.4225)
            val kaabaLng = Math.toRadians(39.8262)

            val userLat = Math.toRadians(lat)
            val userLng = Math.toRadians(lng)

            val deltaLng = kaabaLng - userLng
            val y = sin(deltaLng)
            val x = cos(userLat) * tan(kaabaLat) - sin(userLat) * cos(deltaLng)

            var qibla = Math.toDegrees(atan2(y, x)).toFloat()
            qibla = (qibla + 360f) % 360f
            return qibla
        }
    }
}
