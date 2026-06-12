@UI @215552U
Feature: Categories UI Tests

  # =======================================================================
  # NORMAL USER scenarios
  # =======================================================================

  # -----------------------------------------------------------------------
  # Unauthenticated access – Categories page (UI: /ui/categories)
  # -----------------------------------------------------------------------

  @UI_CAT_USR_004
  Scenario: UI_CAT_USR_004 - Unauthenticated user accessing categories is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/categories"
    Then I should be redirected to the login page