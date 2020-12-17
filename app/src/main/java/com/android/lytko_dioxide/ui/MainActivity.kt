package com.android.lytko_dioxide.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.lytko_dioxide.R
import kotlinx.android.synthetic.main.activity_main.*
import android.os.PowerManager
import androidx.annotation.RequiresApi


class MainActivity : AppCompatActivity() {

    private val isRunningKey: String = "isRunning"
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isRunning = savedInstanceState?.getBoolean(isRunningKey) ?: isServiceRunning()
        updateState()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buttonDoze.setOnClickListener { dozeSettings() }
        } else {
            buttonDoze.isVisible = false
        }

        buttonStart.setOnClickListener { startTracking() }
        buttonStop.setOnClickListener { stopTracking() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isRunning", isRunning)
    }

    private fun isServiceRunning(): Boolean {
        return false
    }

    private fun updateState() {
        updateButtons()
        updateValueFromService()
    }

    private fun updateButtons() {
        buttonStart.isVisible = !isRunning
        buttonStop.isVisible = isRunning
    }

    private fun updateValueFromService() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun dozeSettings() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
        if (!isIgnoringBatteryOptimizations) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 0)
        }
    }

    private fun startTracking() {
        isRunning = true
        updateState()
    }

    private fun stopTracking() {
        isRunning = false
        updateState()
    }
}