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
import com.android.lytko_dioxide.TrackingService.Companion.valueIntentExtraName
import com.android.lytko_dioxide.util.android.broadcastReceiver
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val valueBroadcastReceiver = broadcastReceiver { _, intent ->
        val value = intent?.getFloatExtra(valueIntentExtraName, Float.NaN)
        showValue(value ?: Float.NaN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDozeButton()

        startService(Intent(this, TrackingService::class.java))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(valueBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(valueBroadcastReceiver, IntentFilter(TrackingService.valueIntentName))

        requestValueFromService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, TrackingService::class.java))
    }

    private fun setupDozeButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buttonDisableBatteryOptimizations.setOnClickListener { ignoreBatteryOptimizations() }
        } else {
            buttonDisableBatteryOptimizations.isVisible = false
        }
    }

    private fun requestValueFromService() {
        Timber.d("Request value from service using intent")
        startService(Intent(this, TrackingService::class.java).apply {
            putExtras(bundleOf(TrackingService.requireValueExtraKey to true))
        })
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

    private fun showValue(value: Float) {
        if (value.isNaN()) {
            this.textValue.text = ("Нет данных")
        } else {
            this.textValue.text = ("$value ppm")
        }
    }
}