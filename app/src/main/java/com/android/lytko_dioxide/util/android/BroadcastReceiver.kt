package com.android.lytko_dioxide.util.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

inline fun broadcastReceiver(
    crossinline onReceive: (context: Context?, intent: Intent?) -> Unit
): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onReceive(context, intent)
        }
    }
}