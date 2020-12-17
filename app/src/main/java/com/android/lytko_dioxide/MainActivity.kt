package com.android.lytko_dioxide

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.lytko_dioxide.TrackingService.Companion.statusIntentExtraName
import com.android.lytko_dioxide.TrackingService.Companion.valueIntentExtraName
import com.android.lytko_dioxide.util.android.broadcastReceiver
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val valueBroadcastReceiver = broadcastReceiver { _, intent ->
        val value = intent?.getFloatExtra(valueIntentExtraName, Float.NaN)
        showValue(value ?: Float.NaN)
    }

    private val stateBroadcastReceiver = broadcastReceiver { _, intent ->
        val isRunning = intent?.getBooleanExtra(statusIntentExtraName, false)
        saveServiceStatus(isRunning ?: false)
    }

    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
        syncStateWithService()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(valueBroadcastReceiver)
        unregisterReceiver(stateBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(valueBroadcastReceiver, IntentFilter(TrackingService.valueIntentName))
        registerReceiver(stateBroadcastReceiver, IntentFilter(TrackingService.statusIntentName))

        requestValueFromService()
    }

    private fun setupButtons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buttonDisableBatteryOptimizations.setOnClickListener { ignoreBatteryOptimizations() }
        } else {
            buttonDisableBatteryOptimizations.isVisible = false
        }

        buttonStart.setOnClickListener { startTracking() }
        buttonStop.setOnClickListener { stopTracking() }
    }

    private fun syncStateWithService() {
        Timber.d("syncStateWithService")

        updateButtons()
        requestValueFromService()
    }

    private fun updateButtons() {
        buttonStart.isVisible = !isServiceRunning
        buttonStop.isVisible = isServiceRunning
    }

    private fun requestValueFromService() {
        if (isServiceRunning) {
            Timber.d("Request value from service using intent")
            startService(Intent(this, TrackingService::class.java).apply {
                putExtras(bundleOf(TrackingService.requireValueExtraKey to true))
            })
        } else {
            showValue(Float.NaN)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun ignoreBatteryOptimizations() {
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
        startService(Intent(this, TrackingService::class.java))
    }

    private fun stopTracking() {
        stopService(Intent(this, TrackingService::class.java))
    }

    private fun showValue(value: Float) {
        if (value.isNaN()) {
            this.textValue.text = ("Нет данных")
        } else {
            this.textValue.text = ("$value ppm")
        }
    }

    private fun saveServiceStatus(isRunning: Boolean) {
        this.isServiceRunning = isRunning
        syncStateWithService()
    }
}