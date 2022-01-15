package com.example.androidfalldetection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fall_detection.*


class MainActivity : AppCompatActivity() {
    // create variables of the two class
    private var accelerometer: Accelerometer? = null
    private val fallDetector: FallDetector = FallDetector()
    private val configuration: Configuration = Configuration()
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        }
        setContentView(R.layout.activity_main)
        setFragment(fallDetector)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home ->setFragment(fallDetector)
                R.id.configuration -> setFragment(configuration)
            }

            true
        }

        // instantiate them with this as context
        accelerometer = Accelerometer(this)
        // create a listener for accelerometer
        accelerometer!!.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
                //Log.i("ACCELEROMETER", "($tx, $ty, $ts)")
                val coordinatesAsString = "($tx, $ty, $ts)"
                xyz?.text = coordinatesAsString
                // set the color red if the device moves in positive x axis
                val accelerometerData = AccelerometerData(tx, ty, ts)
                magnitude?.text = accelerometerData.countAcceleration().toString()
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

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fallDetectionFragment, fragment)
            commit()
        }
}

