package com.example.mqtt.services;

import com.example.mqtt.models.dtos.commands.IrrigationCommand;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IrrigationNotifier {
    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private Gson gson;

    private final String IRRIGATION_TOPIC = "hidrobots/command/irrigation/request";

    public String sendIrrigationRequestToDevice(IrrigationCommand command) {
        try {
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(command).getBytes());
            mqttMessage.setQos(2);
            mqttClient.publish(IRRIGATION_TOPIC, mqttMessage);
            return "Message published to topic";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to publish message";
        }
    }
}
