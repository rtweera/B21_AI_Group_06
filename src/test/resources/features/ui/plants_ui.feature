@UI @215552U
Feature: Plants UI Tests

  # =======================================================================
  # NORMAL USER scenarios
  # =======================================================================

  # -----------------------------------------------------------------------
  # Unauthenticated access – Plants page (UI: /ui/plants)
  # -----------------------------------------------------------------------

  @UI_PLT_USR_009
  Scenario: UI_PLT_USR_009 - Unauthenticated user accessing plants is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/plants"
    Then I should be redirected to the login page

  # -----------------------------------------------------------------------
  # Normal user – Sort plants by price (UI: /ui/plants)
  # -----------------------------------------------------------------------

  @UI_PLT_USR_010
  Scenario: UI_PLT_USR_010 - User can sort the plant list by price
    Given I am logged in as user
    When I navigate to the plants page
    And I click the Price column header
    Then the plant list order should change