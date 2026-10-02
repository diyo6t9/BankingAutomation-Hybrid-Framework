@OpenAccount
Feature: Parabank Open New Account

  Scenario: Open new SAVINGS account
    Given User is on Parabank login page
    When User logs in with valid data
    And User opens a new SAVINGS account
    Then User should see new account opened message
    And User logs off