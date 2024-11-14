package com.example.mqtt.services;

import com.example.mqtt.models.dtos.responses.WeatherAnalysisResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;

public class WeatherAnalysisListener {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void handleWeatherAnalysisResponseFromDevice(MqttMessage message) {
        try {

            var response = new String(message.getPayload());

            var responseObj = objectMapper.readValue(response, WeatherAnalysisResponse.class);

            responseObj.setAnalysisDateTime(LocalDateTime.now());

            // El responseObj es un objeto q se peude guardar ya en la base de datos
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }



    }
}
