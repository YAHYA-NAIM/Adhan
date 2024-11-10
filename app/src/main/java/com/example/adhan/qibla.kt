package com.example.adhan

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class qibla : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private lateinit var compassImage: ImageView
    private lateinit var qiblaImage: ImageView

    private val meccaLatitude = 21.4225
    private val meccaLongitude = 39.8262


    private val currentLatitude = 33.552160057335804
    private val currentLongitude = -7.592488364696466

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_qibla, container, false)


        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)


        val mainLayout = view.findViewById<ConstraintLayout>(R.id.cl)
        compassImage = view.findViewById(R.id.compass)


        qiblaImage = ImageView(requireContext()).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.qibla)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }


        mainLayout.addView(qiblaImage)


        val constraintSet = ConstraintSet()
        constraintSet.clone(mainLayout)
        constraintSet.connect(qiblaImage.id, ConstraintSet.TOP, compassImage.id, ConstraintSet.TOP)
        constraintSet.connect(qiblaImage.id, ConstraintSet.BOTTOM, compassImage.id, ConstraintSet.BOTTOM)
        constraintSet.connect(qiblaImage.id, ConstraintSet.START, compassImage.id, ConstraintSet.START)
        constraintSet.connect(qiblaImage.id, ConstraintSet.END, compassImage.id, ConstraintSet.END)
        constraintSet.applyTo(mainLayout)

        return view
    }

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)


            val azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()


            compassImage.rotation = -azimuthInDegrees


            val qiblaDirection = calculateQiblaDirection()
            updateQiblaDirection(qiblaDirection - azimuthInDegrees)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    private fun updateQiblaDirection(degree: Float) {
        qiblaImage.rotation = degree
    }


    private fun calculateQiblaDirection(): Float {
        val lat1 = Math.toRadians(currentLatitude)
        val lon1 = Math.toRadians(currentLongitude)
        val lat2 = Math.toRadians(meccaLatitude)
        val lon2 = Math.toRadians(meccaLongitude)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return Math.toDegrees(atan2(y, x)).toFloat()
    }
}
