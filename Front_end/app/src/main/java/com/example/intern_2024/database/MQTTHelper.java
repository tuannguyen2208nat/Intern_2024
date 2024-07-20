package com.example.intern_2024.database;

import android.content.Context;
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

import java.nio.charset.StandardCharsets;

public class MQTTHelper {
    private MqttAndroidClient mqttAndroidClient;

    private final String username = "tuannguyen2208nat"; // Avoid hardcoding sensitive info
    private final String password = "aio_tXmh36XzkznRt2CHd0TtVyBn62oS"; // Avoid hardcoding sensitive info
    private final String link = "tuannguyen2208nat/feeds/status";
    private final String clientId = "12345678";
    private final String serverUri = "tcp://io.adafruit.com:1883";

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

    public void sendData(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setQos(0);
        msg.setRetained(false);

        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        msg.setPayload(payload);

        try {
            mqttAndroidClient.publish(topic, msg);
            Log.i("MQTT", "Message published to topic: " + topic);
        } catch (MqttException e) {
            Log.e("MQTT", "Failed to publish message", e);
        }
    }
}
