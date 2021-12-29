package com.example.androidfalldetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    // create variables of the two class
    private var accelerometer: Accelerometer? = null
    private val fallDetector: FallDetector = FallDetector()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fallDetectionFragment, fallDetector)
            commit()
        }
        // instantiate them with this as context
        accelerometer = Accelerometer(this)
        // create a listener for accelerometer
        accelerometer!!.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
                //Log.i("ACCELEROMETER", "($tx, $ty, $ts)")
                val coordinatesAsString = "($tx, $ty, $ts)"
                xyz.text = coordinatesAsString
                // set the color red if the device moves in positive x axis
                val accelerometerData = AccelerometerData(tx, ty, ts)
                magnitude.text = accelerometerData.countAcceleration().toString()
                fallDetector.detectFall(accelerometerData)
            }
        })

    }

    // create on resume method
    override fun onResume() {
        super.onResume()

        // this will send notification to
        // both the sensors to register
        accelerometer!!.register()
    }

    // create on pause method
    override fun onPause() {
        super.onPause()

        // this will send notification in
        // both the sensors to unregister
        accelerometer!!.unregister()
    }
}

