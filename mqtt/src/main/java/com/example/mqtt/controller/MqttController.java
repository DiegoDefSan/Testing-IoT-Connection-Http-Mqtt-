package com.example.mqtt.controller;

import com.example.mqtt.models.dtos.commands.IrrigationCommand;
import com.example.mqtt.models.dtos.commands.WeatherAnalysisCommand;
import com.example.mqtt.services.IrrigationNotifier;
import com.example.mqtt.services.WeatherAnalysisNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    @Autowired
    private WeatherAnalysisNotifier weatherAnalysisNotifier;

    @Autowired
    private IrrigationNotifier irrigationNotifier;

    @GetMapping
    public String index() {
        return "Welcome to MQTT Controller";
    }

    @PostMapping("/weather_analysis")
    public ResponseEntity<String> weatherAnalysis(@RequestBody WeatherAnalysisCommand command) {
        var response = weatherAnalysisNotifier.sendAnalysisRequestToDevice(command);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/irrigation")
    public ResponseEntity<String> irrigation(@RequestBody IrrigationCommand command) {
        var response = irrigationNotifier.sendIrrigationRequestToDevice(command);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
