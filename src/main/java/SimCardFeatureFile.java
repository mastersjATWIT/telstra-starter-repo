Feature: SIM Card Activation
  As a service provider
  I want to activate SIM cards for customers
  So that they can use their mobile devices

  Scenario: Successful SIM Card Activation
    Given the SIM card actuator service is running
    When I submit an activation request with ICCID "1255789453849037777" and email "customer@example.com"
    Then the activation should be successful
    And when I query the database for record with ID 1
    Then I should see ICCID "1255789453849037777", email "customer@example.com", and active status as true

  Scenario: Failed SIM Card Activation
    Given the SIM card actuator service is running
    When I submit an activation request with ICCID "8944500102198304826" and email "customer2@example.com"
    Then the activation should not be successful
    And when I query the database for record with ID 2
    Then I should see ICCID "8944500102198304826", email "customer2@example.com", and active status as false