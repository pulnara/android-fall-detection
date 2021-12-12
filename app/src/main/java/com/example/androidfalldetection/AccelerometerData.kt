package com.example.androidfalldetection

import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.PI

class AccelerometerData(val x: Float, val y: Float, val z: Float) {

    fun countRoll(): Float {
        return atan(y / sqrt( x * x + z * z  )) * 180 / PI.toFloat()
    }

    fun countPitch(): Float {
        return atan(x / sqrt( y * y + z * z  )) * 180 / PI.toFloat()
    }

    fun countAlpha(): Float {
        return sqrt(x * x + y * y + z * z)
    }
}