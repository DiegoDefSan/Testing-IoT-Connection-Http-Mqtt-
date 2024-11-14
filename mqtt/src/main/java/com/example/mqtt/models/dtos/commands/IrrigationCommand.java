package com.example.mqtt.models.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IrrigationCommand {
    private String deviceCode;
    private boolean activateIrrigation;
}
