package com.example.androidfalldetection

import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.PI

class AccelerometerData(val x: Float, val y: Float, val z: Float) {
    fun countAcceleration(): Float {
        return sqrt(x * x + y * y + z * z)
    }
}