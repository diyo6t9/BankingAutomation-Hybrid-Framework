@Sanity @BillPay
Feature: Parabank Bill Pay

  Scenario: Valid bill payment
    Given User is on Parabank login page
    When User logs in with valid data
    And User pays bill with valid data
    Then User should see bill payment complete message
    And User logs off