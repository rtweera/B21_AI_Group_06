@UI
Feature: Plants Access UI Tests

  @UI_PLT_USR_009 @215552U
  Scenario: Unauthenticated user accessing plants is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/plants"
    Then I should be redirected to the login page

  @UI_PLT_USR_010 @215552U
  Scenario: User can sort the plant list by price
    Given I am logged in as user
    When I navigate to the plants page
    And I click the Price column header
    Then the plant list order should change