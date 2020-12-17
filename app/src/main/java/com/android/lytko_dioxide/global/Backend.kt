package com.android.lytko_dioxide.global

import androidx.lifecycle.MutableLiveData
import com.android.lytko_dioxideutil.mqtt.awaitConnectWith
import com.android.lytko_dioxideutil.mqtt.awaitSubscribe
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber


object Backend {

    private val broker = "lytko.com"
    private val port = "1883"

    private lateinit var clientId: String
    private lateinit var client: MqttAndroidClient


    /* --- API --- */

    fun initialize() {
        clientId = MqttClient.generateClientId()

        client = MqttAndroidClient(App.appContext, "tcp://$broker:$port", clientId)
        client.setCallback(object : MqttCallback {

            override fun connectionLost(cause: Throwable) {
                Timber.e("Connection lost")
            }

            override fun messageArrived(topic: String, message: MqttMessage) =
                dispatchMessage(topic, message.payload)

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Timber.i("Delivery complete")
            }
        })
    }

    suspend fun connect() {
        client.awaitConnectWith {
            this.userName = "Grigory"
            this.password = "GrigoryPass".toCharArray()
        }
    }

    fun disconnect() {
        if (client.isConnected) {
            client.disconnect()
        }
    }


    /* --- PRIVATE --- */

    private fun dispatchMessage(topic: String, message: ByteArray) {

    }
}