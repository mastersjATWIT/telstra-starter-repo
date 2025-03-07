package com.example.simactivation.dto;

// Request DTO for incoming activation requests
public class ActivationRequest {
    private String iccid;
    private String customerEmail;
    
    // Getters and setters
    public String getIccid() {
        return iccid;
    }
    
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}

// Request DTO for calls to the actuator service
public class ActuatorRequest {
    private String iccid;
    
    // Getters and setters
    public String getIccid() {
        return iccid;
    }
    
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}

// Response DTO from the actuator service
public class ActuatorResponse {
    private boolean success;
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}