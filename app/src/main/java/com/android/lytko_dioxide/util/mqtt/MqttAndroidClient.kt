package com.android.lytko_dioxideutil.mqtt

import com.android.lytko_dioxide.util.mqtt.toMqttMessage
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.coroutines.*

suspend inline fun MqttAndroidClient.awaitConnectWith(
    crossinline options: MqttConnectOptions.() -> Unit
) = suspendCoroutine<Unit> { continuation ->
    connect(MqttConnectOptions().apply { options(this) }).actionCallback = object: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            continuation.resume(Unit)
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            continuation.resumeWithException(exception)
        }
    }
}

suspend inline fun MqttAndroidClient.awaitSubscribe(topic: String) = suspendCoroutine<Unit> { continuation ->
    subscribe(topic, 0, null, object: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            continuation.resume(Unit)
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            continuation.resumeWithException(exception)
        }
    })
}

inline fun MqttAndroidClient.subscribe(topic: String, qos: Int = 0) = subscribe(topic, qos)

suspend inline fun MqttAndroidClient.awaitPublish(topic: String, message: String) = suspendCoroutine<Unit> { continuation ->
    publish(topic, message.toMqttMessage(), null, object: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            continuation.resume(Unit)
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            continuation.resumeWithException(exception)
        }
    })
}