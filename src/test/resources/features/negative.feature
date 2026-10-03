@Sanity @Negative
Feature: Parabank negative scenarios

  # ---------------- LOGIN ----------------

  @LoginNegative @InvalidLogin
  Scenario Outline: Login fails with invalid credentials
    Given User is on Parabank login page
    When User logs in with username "<username>" and password "<password>"
    Then User should see login error "<message>"

    Examples:
      | username    | password  | message                                          |
      | diyo        | wrongpass | The username and password could not be verified. |
      | wronguser   | diyo     | The username and password could not be verified. |
      | wronguser   | wrongpass | The username and password could not be verified. |

  @LoginNegative @EmptyLogin
  Scenario Outline: Login fails when a mandatory field is empty
    Given User is on Parabank login page
    When User logs in with username "<username>" and password "<password>"
    Then User should see login error "Please enter a username and password."

    Examples:
      | username | password |
      |          |          |
      | diyo     |          |
      |          | hello    |

      # ---------------- TRANSFER FUNDS ----------------

  @TransferNegative @InvalidAmount
  Scenario Outline: Transfer is rejected for an invalid amount
    Given User is on Parabank login page
    When User logs in with valid data
    Then User should see Accounts Overview
    And User navigates to Transfer Funds page
    When User transfers the amount "<amount>"
    Then Transfer should not be successful
    And User logs off

    Examples:
      | amount |
      |        |
      | abc    |




  @BillPayNegative @InvalidBillData
  Scenario Outline: Bill payment fails with invalid account or amount
    Given User is on Parabank login page
    When User logs in with valid data
    And User submits bill pay with account "<account>", verify account "<verify>" and amount "<amount>"
    Then User should see bill pay error "<message>"
    And User logs off

    Examples:
      | account | verify | amount | message                           |
      | 12345   | 54321  | 100    | The account numbers do not match. |
      | abide   | abide  | 100    | Please enter a valid number.      |
      | 12345   | 12345  | abc    | Please enter a valid amount.      |
      | 12345   | 12345  |        | The amount cannot be empty.       |

  @BillPayNegative @EmptyForm
  Scenario: Bill payment fails when the whole form is empty
    Given User is on Parabank login page
    When User logs in with valid data
    And User submits the bill pay form without entering any data
    Then User should see bill pay error "Payee name is required."
    And User should see bill pay error "Address is required."
    And User should see bill pay error "City is required."
    And User should see bill pay error "State is required."
    And User should see bill pay error "Zip Code is required."
    And User should see bill pay error "Phone number is required."
    And User should see bill pay error "Account number is required."
    And User should see bill pay error "The amount cannot be empty."
    And User logs off

