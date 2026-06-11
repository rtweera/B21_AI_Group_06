@UI
Feature: Authentication UI Tests

  @UI_AUTH_USR_005 @215552U
  Scenario: User logout ends the session
    Given I am logged in as user
    When I click the Logout button
    Then I should see a logout success message
    And I should be redirected to the login page