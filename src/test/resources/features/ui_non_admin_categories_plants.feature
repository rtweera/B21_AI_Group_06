Feature: Non-admin categories and plants access

  Background:
    Given non admin user is logged in

  @UI @User @215564H
  Scenario: UI_CAT_USR_002 Verify no category found message
    When user navigates to Categories page
    Then if categories are empty "No category found" should be displayed

  @UI @User @215564H
  Scenario: UI_CAT_USR_003 Verify non-admin cannot edit or delete categories
    When user navigates to Categories page
    Then category edit and delete buttons should not be clickable

  @UI @User @215564H
  Scenario: UI_PLT_USR_001 Verify non-admin can view plant list
    When user navigates to Plants page
    Then plant list should be displayed as a table

  @UI @User @215564H
  Scenario: UI_PLT_USR_002 Verify non-admin does not see Add Plant button
    When user navigates to Plants page
    Then Add Plant button should not be visible

  @UI @User @215564H
  Scenario: UI_PLT_USR_003 Verify non-admin can search a relevant plant
    When user navigates to Plants page
    And user searches plant "plant"
    Then searched plant "plant" should be displayed

  @UI @User @215564H
  Scenario: UI_PLT_USR_003 Verify non-admin can search a relevant plant
    When user navigates to Plants page
    And user searches plant "plant 2"
    Then searched plant "plant 2" should be displayed