@UI
Feature: Categories Access UI Tests

  @UI_CAT_USR_004 @215552U
  Scenario: Unauthenticated user accessing categories is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/categories"
    Then I should be redirected to the login page