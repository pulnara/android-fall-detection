package com.example.androidfalldetection

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfalldetection.ml.FallDetectionModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : AppCompatActivity() {
    // create variables of the two class
    private var accelerometer: Accelerometer? = null
    private var gyroscope: Gyroscope? = null
    private lateinit var model: FallDetectionModel
    private var measurments_count = 0
    private var measurements: FloatArray = FloatArray(4656)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = FallDetectionModel.newInstance(this)

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

                measurements
                measurments_count += 1

                if(measurments_count > 300) {

                    val mes_len = measurements.count()
                    while(mes_len < 1552){
                        measurements[mes_len - 1] = 64F
                    }

                    val tbuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1552, 3), DataType.FLOAT32)
                    tbuffer.loadArray(measurements)
                    val byteBuffer = tbuffer.buffer

                    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1552, 3), DataType.FLOAT32)
                    inputFeature0.loadBuffer(byteBuffer)

                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                    Log.i("MODEL", outputs.toString())
                    Log.i("MODEL_F", outputFeature0.toString())

                    measurments_count = 0
                    measurements = FloatArray(4656)
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

        model = FallDetectionModel.newInstance(this)

        // this will send notification to
        // both the sensors to register
        accelerometer!!.register()
        gyroscope!!.register()
    }

    // create on pause method
    override fun onPause() {
        super.onPause()

        model.close()

        // this will send notification in
        // both the sensors to unregister
        accelerometer!!.unregister()
        gyroscope!!.unregister()
    }
}

