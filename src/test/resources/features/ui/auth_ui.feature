@UI @215552U
Feature: Authentication UI Tests

  # =======================================================================
  # NORMAL USER scenarios
  # =======================================================================

  # -----------------------------------------------------------------------
  # Normal user – Logout ends session (UI: /ui/login)
  # -----------------------------------------------------------------------

  @UI_AUTH_USR_005
  Scenario: UI_AUTH_USR_005 - User logout ends the session
    Given I am logged in as user
    When I click the Logout button
    Then I should see a logout success message
    And I should be redirected to the login page