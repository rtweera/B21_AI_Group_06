@UI
Feature: Sales Access UI Tests

  @UI_SLS_USR_001 @215552U
  Scenario: Unauthenticated user accessing sales is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/sales"
    Then I should be redirected to the login page