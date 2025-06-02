package com.example.faunafinder.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf

class ProximitySensorManager(context: Context): SensorEventListener {
private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    //true = algo está cerca | false = libre
    val isNear = mutableStateOf(false)

    fun startListening(){
        proximitySensor?.also{
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening(){
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?){
        event?.let{
            // Para la mayoría de sensores: 0 = cerca, >0 = lejos
            isNear.value = it.values[0] < proximitySensor?.maximumRange ?: 5f
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}