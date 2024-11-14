package com.example.mqtt.configuration;

import com.example.mqtt.services.WeatherAnalysisListener;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private static final String MQTT_BROKER_URL = "tcp://mqtt-dashboard.com:1883";
    private static final String CLIENT_ID = "hodrobots_mqtt_client";

    private static final String[] SUBSCRIBE_TOPICS = {
            "hidrobots/soil_irrigation/responses",
            "hidrobots/weather_analysis/responses"
    };

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(MQTT_BROKER_URL, CLIENT_ID, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Message received on topic: " + topic + ". Content: " + new String(message.getPayload()));

                // Procesamiento según el topic
                switch (topic) {
                    case "hidrobots/soil_irrigation/responses":
                        // Procesa mensajes para el topic de comandos
                        // handleCommandMessage(message);
                        break;
                    case "hidrobots/weather_analysis/responses":
                        System.out.println("Handling weather analysis response");
                        WeatherAnalysisListener.handleWeatherAnalysisResponseFromDevice(message);
                        break;
                    default:
                        System.out.println("Unrecognized topic: " + topic);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Esta función es opcional para suscripciones
            }
        });

        client.connect(options);
        for (String topic : SUBSCRIBE_TOPICS) {
            client.subscribe(topic);
        }
        return client;
    }
}
