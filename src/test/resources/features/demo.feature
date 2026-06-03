Feature: demo feature

  Scenario: user login with valid credentials
    Given user opens login page
    When user enters correct username and password
    And click login button
    Then user should navigate to home page

  Scenario: user login with invalid credentials
    Given user opens login page
    When user enters incorrect username and password
    And click login button
    Then user should see error message

