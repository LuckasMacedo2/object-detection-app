package com.lucas.myapp

import com.lucas.myapp.Data.DetectedObject

interface ApiCallback {
    fun onApiResult(result: List<DetectedObject>?)
}
