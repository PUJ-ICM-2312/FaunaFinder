package com.example.faunafinder.sensors

import android.content.Context
import android.hardware.*
import androidx.compose.runtime.mutableStateOf
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CompassSensorManager(context: Context) : SensorEventListener{
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    val azimuth = mutableStateOf(0f) //Dirección en grados (0-360°)

    fun startListening() {
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening(){
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?){
        when(event?.sensor?.type){
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
        }
        val g = gravity
        val m = geomagnetic
        if(g != null &&m != null){
            val R = FloatArray(9)
            val I = FloatArray(9)
            if(SensorManager.getRotationMatrix(R,I,g,m)){
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                val azimuthRad = orientation[0]
                val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                azimuth.value = (azimuthDeg + 360) % 360 //Lo normaliza a 0-360°
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}