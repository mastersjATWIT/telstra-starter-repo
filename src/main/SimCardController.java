package com.example.simactivation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.simactivation.dto.ActivationRequest;
import com.example.simactivation.dto.ActuatorRequest;
import com.example.simactivation.dto.ActuatorResponse;

@RestController
public class SimActivationController {

    private final RestTemplate restTemplate;
    private final String actuatorUrl = "http://localhost:8444/actuate";

    @Autowired
    public SimActivationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateSim(@RequestBody ActivationRequest request) {
        System.out.println("Received activation request for ICCID: " + request.getIccid() + 
                           ", Customer email: " + request.getCustomerEmail());
        
        try {
            // Create request for actuator service
            ActuatorRequest actuatorRequest = new ActuatorRequest();
            actuatorRequest.setIccid(request.getIccid());
            
            // Call actuator service
            ActuatorResponse response = restTemplate.postForObject(
                actuatorUrl, 
                actuatorRequest, 
                ActuatorResponse.class
            );
            
            // Log the result
            if (response.isSuccess()) {
                System.out.println("SIM activation successful for ICCID: " + request.getIccid());
            } else {
                System.out.println("SIM activation failed for ICCID: " + request.getIccid());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error during SIM activation: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Activation service error: " + e.getMessage());
        }
    }
}