package stepDefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.simactivation.dto.ActivationRequest;
import com.example.simactivation.dto.ActuatorResponse;
import com.example.simactivation.dto.SimQueryResponse;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimCardActivatorStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<ActuatorResponse> activationResponse;
    private ResponseEntity<SimQueryResponse> queryResponse;
    private String currentIccid;
    private String currentEmail;

    @Given("the SIM card actuator service is running")
    public void theSimCardActuatorServiceIsRunning() {
        // We'll assume the actuator service is running for these tests
        // This step is more of a documentation step, but in a real scenario
        // we might want to verify the service is actually running
        System.out.println("Assuming SIM card actuator service is running at http://localhost:8444/actuate");
    }

    @When("I submit an activation request with ICCID {string} and email {string}")
    public void iSubmitAnActivationRequestWithICCIDAndEmail(String iccid, String email) {
        this.currentIccid = iccid;
        this.currentEmail = email;

        ActivationRequest request = new ActivationRequest();
        request.setIccid(iccid);
        request.setCustomerEmail(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ActivationRequest> entity = new HttpEntity<>(request, headers);

        activationResponse = restTemplate.exchange(
                "http://localhost:8080/activate",
                HttpMethod.POST,
                entity,
                ActuatorResponse.class);

        System.out.println("Submitted activation request for ICCID: " + iccid + ", Email: " + email);
    }

    @Then("the activation should be successful")
    public void theActivationShouldBeSuccessful() {
        assertNotNull("Activation response should not be null", activationResponse);
        assertNotNull("Activation response body should not be null", activationResponse.getBody());
        assertTrue("Activation should be successful", activationResponse.getBody().isSuccess());
        System.out.println("Activation was successful as expected");
    }

    @Then("the activation should not be successful")
    public void theActivationShouldNotBeSuccessful() {
        assertNotNull("Activation response should not be null", activationResponse);
        assertNotNull("Activation response body should not be null", activationResponse.getBody());
        assertFalse("Activation should not be successful", activationResponse.getBody().isSuccess());
        System.out.println("Activation was not successful as expected");
    }

    @When("I query the database for record with ID {long}")
    public void iQueryTheDatabaseForRecordWithID(Long simCardId) {
        queryResponse = restTemplate.getForEntity(
                "http://localhost:8080/query?simCardId=" + simCardId,
                SimQueryResponse.class);
        
        System.out.println("Queried database for record with ID: " + simCardId);
    }

    @Then("I should see ICCID {string}, email {string}, and active status as {boolean}")
    public void iShouldSeeICCIDEmailAndActiveStatusAs(String expectedIccid, String expectedEmail, boolean expectedActiveStatus) {
        assertNotNull("Query response should not be null", queryResponse);
        assertNotNull("Query response body should not be null", queryResponse.getBody());
        
        SimQueryResponse response = queryResponse.getBody();
        assertEquals("ICCID should match", expectedIccid, response.getIccid());
        assertEquals("Email should match", expectedEmail, response.getCustomerEmail());
        assertEquals("Active status should match", expectedActiveStatus, response.isActive());
        
        System.out.println("Verified database record has expected values:");
        System.out.println("ICCID: " + response.getIccid());
        System.out.println("Email: " + response.getCustomerEmail());
        System.out.println("Active: " + response.isActive());
    }
}