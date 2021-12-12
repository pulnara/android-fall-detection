package com.example.androidfalldetection

import kotlin.math.atan
import kotlin.math.sqrt

class AccelerometerData(val x: Float, val y: Float, val z: Float) {

    fun countRoll(): Float {
        return atan(y / sqrt( x * x + z * z  ))
    }

    fun countPitch(): Float {
        return atan(x / sqrt( y * y + z * z  ))
    }

    fun countAlpha(): Float {
        return sqrt(x * x + y * y + z * z)
    }
}