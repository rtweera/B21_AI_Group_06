@UI @215527A
Feature: UI category permissions

  @UI_CAT_ADM_001
  Scenario: UI_CAT_ADM_001 - Admin can see the Add A Category button
    Given I am logged in as an admin user
    When I open the Categories page
    Then the Add A Category button should be visible

  @UI_CAT_ADM_002
  Scenario: UI_CAT_ADM_002 - Admin can see edit and delete buttons for every category
    Given I am logged in as an admin user
    When I open the Categories page
    Then every category row should show edit and delete actions

  @UI_CAT_ADM_003
  Scenario: UI_CAT_ADM_003 - Category search filters by an existing category name
    Given I am logged in as an admin user
    And I open the Categories page
    When I search for an existing category by name
    Then only categories matching that name should be visible

  @UI_CAT_USR_001
  Scenario: UI_CAT_USR_001 - Normal user cannot see category management controls
    Given I am logged in as a normal user
    When I open the Categories page
    Then no category management buttons should be visible
