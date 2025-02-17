package com.example.enturcase.utils

import android.util.Log

object Logger {
    private const val TAG = "NTUR"
    fun debug(msg: String) {
        Log.d(TAG, msg)
    }
}