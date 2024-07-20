package com.example.intern_2024.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    private String username = "tuannguyen2208nat";
    private String password = "aio_CmYo32Wm3opKsyN7aSAitKqOOiry";
    public final String link = "tuannguyen2208nat/feeds/status";
    private String clientId = "12345678";
    private String serverUri = "tcp://io.adafruit.com:1883";

    public MQTTHelper(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("MQTT", "Connected to: " + serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MQTT", "Connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MQTT", "Message arrived. Topic: " + topic + ", Message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("MQTT", "Delivery complete for message ID: " + token.getMessageId());
            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Connected successfully");
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Failed to connect", exception);
                }
            });
        } catch (MqttException ex) {
            Log.e("MQTT", "Exception during connect", ex);
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(link, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Subscribed to topic: " + link);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Failed to subscribe", exception);
                }
            });
        } catch (MqttException ex) {
            Log.e("MQTT", "Exception during subscribe", ex);
        }
    }

    public void disconnect() {
        try {
            if (mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
                Log.i("MQTT", "Disconnected successfully");
            }
        } catch (MqttException ex) {
            Log.e("MQTT", "Exception during disconnect", ex);
        }
    }
}