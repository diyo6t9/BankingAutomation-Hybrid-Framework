@Transfer
Feature: Fund Transfer

  Scenario: Transfer amount between accounts
    Given User is on Parabank login page
    When User logs in with valid data
    Then User should see Accounts Overview
    And User navigates to Transfer Funds page
    When User transfers amount
    Then Transfer should be successful
    And User logs off