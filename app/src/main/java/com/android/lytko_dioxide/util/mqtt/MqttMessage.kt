package com.android.lytko_dioxide.util.mqtt

import org.eclipse.paho.client.mqttv3.MqttMessage

fun String.toMqttMessage(qos: Int = 0) = MqttMessage(this.toByteArray()).apply { this.qos = qos }