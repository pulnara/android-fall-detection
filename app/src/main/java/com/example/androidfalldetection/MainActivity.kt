package com.example.androidfalldetection

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    // create variables of the two class
    private var accelerometer: Accelerometer? = null
    private val fallDetector: FallDetector = FallDetector()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // instantiate them with this as context
        accelerometer = Accelerometer(this)
        // create a listener for accelerometer
        accelerometer!!.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
                //Log.i("ACCELEROMETER", "($tx, $ty, $ts)");
                // set the color red if the device moves in positive x axis
                val accelerometerData = AccelerometerData(tx, ty, ts)
                if (fallDetector.fallDetected(accelerometerData))
                {
                    window.decorView.setBackgroundColor(Color.RED)
                }
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

