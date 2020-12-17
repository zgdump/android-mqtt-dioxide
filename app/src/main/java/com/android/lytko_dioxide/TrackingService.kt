package com.android.lytko_dioxide

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber

class TrackingService : Service() {
    
    companion object {
        
        private const val mqttUsername = "Grigory"
        private const val mqttPassword = "GrigoryPass"
        
        private const val mqttBroker = "lytko.com"
        private const val mqttPort = "1883"
        
        private const val mqttDeviceId = 0
        
        const val valueIntentName = "value"
        const val valueIntentExtraName = "ppm"
    }
    
    private lateinit var mqttClient: MqttAndroidClient
    
    override fun onCreate() {
        Timber.i("TrackingService#onCreate")
        
        initialize()
        connect()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("TrackingService#onStartCommand, has intent ${intent != null}")
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    
    override fun onDestroy() {
        Timber.i("TrackingService#onDestroy")
        
        disconnect()
    }
    
    private fun initialize() {
        Timber.i("TrackingService#initialize")
        
        mqttClient = MqttAndroidClient(
            App.appContext,
            "tcp://$mqttBroker:$mqttPort",
            MqttClient.generateClientId()
        )
        mqttClient.setCallback(object : MqttCallback {
    
            override fun connectionLost(cause: Throwable) {
                Timber.e("Connection lost")
            }
    
            override fun messageArrived(topic: String, message: MqttMessage) {
                Timber.i("Message arrived from $topic")
                valueReceived(String(message.payload).toFloat())
            }
    
            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Timber.i("Delivery complete")
            }
        })
    }
    
    private fun connect() {
        Timber.i("TrackingService#connect")
        
        mqttClient.connect(MqttConnectOptions().apply {
            this.userName = mqttUsername
            this.password = mqttPassword.toCharArray()
        }).actionCallback = object : IMqttActionListener {
            
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Timber.i("Successfully connected to server")
                subscribeTopic()
            }
            
            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                TODO("Unimplemented. Failure on connect")
            }
        }
    }
    
    private fun subscribeTopic() {
        mqttClient.subscribe(
            "$mqttUsername/health/lytko/$mqttDeviceId/value",
            0,
            null,
            object : IMqttActionListener {
        
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Timber.i("Successfully subscribed to topic")
                }
        
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    TODO("Unimplemented. Failure on subscribe")
                }
            }
        )
    }
    
    private fun valueReceived(value: Float) {
        Timber.i("Value received $value")
        
        LocalBroadcastManager
            .getInstance(this)
            .sendBroadcast(Intent(valueIntentName).apply { putExtra(valueIntentExtraName, value) })
    }
    
    private fun disconnect() {
        Timber.i("TrackingService#Disconnect")
        mqttClient.disconnect()
    }
}