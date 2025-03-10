package com.example.simactivation.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.simactivation.dto.ActivationRequest;
import com.example.simactivation.dto.ActuatorRequest;
import com.example.simactivation.dto.ActuatorResponse;
import com.example.simactivation.dto.SimQueryResponse;
import com.example.simactivation.entity.SimActivation;
import com.example.simactivation.repository.SimActivationRepository;

@RestController
public class SimActivationController {

    private final RestTemplate restTemplate;
    private final SimActivationRepository simActivationRepository;
    private final String actuatorUrl = "http://localhost:8444/actuate";

    @Autowired
    public SimActivationController(RestTemplate restTemplate, SimActivationRepository simActivationRepository) {
        this.restTemplate = restTemplate;
        this.simActivationRepository = simActivationRepository;
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
            
            boolean isSuccess = response.isSuccess();
            
            // Log the result
            if (isSuccess) {
                System.out.println("SIM activation successful for ICCID: " + request.getIccid());
            } else {
                System.out.println("SIM activation failed for ICCID: " + request.getIccid());
            }
            
            // Save record to database
            SimActivation simActivation = new SimActivation(
                request.getIccid(),
                request.getCustomerEmail(),
                isSuccess
            );
            
            simActivationRepository.save(simActivation);
            System.out.println("Saved activation record to database with ID: " + simActivation.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error during SIM activation: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Activation service error: " + e.getMessage());
        }
    }
    
    @GetMapping("/query")
    public ResponseEntity<?> querySimActivation(@RequestParam Long simCardId) {
        System.out.println("Received query for SIM activation record with ID: " + simCardId);
        
        Optional<SimActivation> simActivationOpt = simActivationRepository.findById(simCardId);
        
        if (simActivationOpt.isPresent()) {
            SimActivation simActivation = simActivationOpt.get();
            SimQueryResponse response = new SimQueryResponse(
                simActivation.getIccid(),
                simActivation.getCustomerEmail(),
                simActivation.isActive()
            );
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}