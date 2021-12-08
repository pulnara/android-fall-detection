package com.example.androidfalldetection

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    // create variables of the two class
    private var accelerometer: Accelerometer? = null
    private var gyroscope: Gyroscope? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // instantiate them with this as context
        accelerometer = Accelerometer(this)
        gyroscope = Gyroscope(this)

        // create a listener for accelerometer
        accelerometer!!.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
                Log.i("ACCELEROMETER", "($tx, $ty, $ts)");
                // set the color red if the device moves in positive x axis
                if (tx > 1.0f) {
                    window.decorView.setBackgroundColor(Color.RED)
                } else if (tx < -1.0f) {
                    window.decorView.setBackgroundColor(Color.BLUE)
                }
            }
        })

        // create a listener for gyroscope
        gyroscope!!.setListener(object : Gyroscope.Listener {
            // on rotation method of gyroscope
            override fun onRotation(rx: Float, ry: Float, rz: Float) {
                // set the color green if the device rotates on positive z axis
                Log.i("GYROSCOPE", "($rx, $ry, $rz)");
                if (rz > 1.0f) {
                    window.decorView.setBackgroundColor(Color.GREEN)
                } else if (rz < -1.0f) {
                    window.decorView.setBackgroundColor(Color.YELLOW)
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
        gyroscope!!.register()
    }

    // create on pause method
    override fun onPause() {
        super.onPause()

        // this will send notification in
        // both the sensors to unregister
        accelerometer!!.unregister()
        gyroscope!!.unregister()
    }
}

