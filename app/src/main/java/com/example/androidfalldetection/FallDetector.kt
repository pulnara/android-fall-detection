package com.example.androidfalldetection

import android.util.Log

class FallDetector {

    fun fallDetected(ad: AccelerometerData): Boolean {
        val roll = ad.countRoll()
        val pitch = ad.countPitch()
        val alpha = ad.countAlpha()
        Log.i("ROLL PITCH ALPHA", "($roll, $pitch, $alpha)");
        return backwardFallDetected(ad.z, roll, pitch, alpha) || forwardFallDetected(ad.z, roll, pitch, alpha)
    }

    fun backwardFallDetected(z: Float, roll: Float, pitch: Float, alpha: Float): Boolean {
        return (z <= -0.5 && pitch >= 45.0 && roll >= 40f && roll <= 50f && alpha >= 0.8)

    }

    fun forwardFallDetected(z: Float, roll: Float, pitch: Float, alpha: Float): Boolean {
        return (z >= -0.5 && pitch <= -45.0 && roll >= 40f && roll <= 50f && alpha >= 1.0)
    }


}