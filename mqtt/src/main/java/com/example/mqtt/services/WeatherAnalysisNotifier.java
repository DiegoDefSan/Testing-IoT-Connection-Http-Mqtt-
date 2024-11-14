package com.example.mqtt.services;

import com.example.mqtt.models.dtos.commands.WeatherAnalysisCommand;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherAnalysisNotifier {
    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private Gson gson;

    private final String WEATHER_ANALYSIS_TOPIC = "hidrobots/command/weather_analysis/request";

    public String sendAnalysisRequestToDevice(WeatherAnalysisCommand command) {
        try {
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(command).getBytes());
            mqttMessage.setQos(2);
            mqttClient.publish(WEATHER_ANALYSIS_TOPIC, mqttMessage);
            return "Message published to topic";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to publish message";
        }
    }
}
