@Sanity
Feature: Parabank Login

  Scenario: Valid login
    Given User is on Parabank login page
    When User logs in with valid data
    Then User should see Accounts Overview
    And User logs off