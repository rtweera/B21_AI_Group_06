@UI
Feature: Categories UI Tests

  # -----------------------------------------------------------------------
  # 215527A – Admin category visibility and search
  # -----------------------------------------------------------------------

  @UI_CAT_ADM_001 @215527A
  Scenario: [UI_CAT_ADM_001] Admin can see the Add A Category button
    Given I am logged in as an admin user
    When I open the Categories page
    Then the Add A Category button should be visible

  @UI_CAT_ADM_002 @215527A
  Scenario: [UI_CAT_ADM_002] Admin can see edit and delete buttons for every category
    Given I am logged in as an admin user
    When I open the Categories page
    Then every category row should show edit and delete actions

  @UI_CAT_ADM_003 @215527A
  Scenario: [UI_CAT_ADM_003] Category search filters by an existing category name
    Given I am logged in as an admin user
    And I open the Categories page
    When I search for an existing category by name
    Then only categories matching that name should be visible

  # -----------------------------------------------------------------------
  # 215564H – Admin category CRUD
  # -----------------------------------------------------------------------

  @UI_CAT_ADM_004 @215564H
  Scenario: [UI_CAT_ADM_004] Verify Add A Category button is clickable
    Given admin user is logged in
    When admin navigates to Categories page
    And admin clicks Add A Category button
    Then admin should be redirected to Add Category page

  @UI_CAT_ADM_005 @215564H
  Scenario: [UI_CAT_ADM_005] Verify admin can add category name
    Given admin user is logged in
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "TestCat"
    Then category name should be entered successfully

  @UI_CAT_ADM_006 @215564H
  Scenario: [UI_CAT_ADM_006] Verify Cancel redirects to Categories Page
    Given admin user is logged in
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin clicks Cancel button
    Then admin should be redirected to Categories page

  @UI_CAT_ADM_007 @215564H
  Scenario: [UI_CAT_ADM_007] Verify admin can save a new category
    Given admin user is logged in
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "TestCat"
    And admin clicks Save button
    Then saved category "TestCat" should be visible in category list

  @UI_CAT_ADM_008 @215564H
  Scenario: [UI_CAT_ADM_008] Verify admin can create sub category
    Given admin user is logged in
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "SubCat"
    And admin selects parent category "TestCat"
    And admin clicks Save button
    Then sub category "SubCat" should be visible with parent category "TestCat"

  # -----------------------------------------------------------------------
  # 215527A – Normal user category access
  # -----------------------------------------------------------------------

  @UI_CAT_USR_001 @215527A
  Scenario: [UI_CAT_USR_001] Normal user cannot see category management controls
    Given I am logged in as a normal user
    When I open the Categories page
    Then no category management buttons should be visible

  # -----------------------------------------------------------------------
  # 215564H – Non-admin category restrictions
  # -----------------------------------------------------------------------

  @UI_CAT_USR_002 @215564H
  Scenario: [UI_CAT_USR_002] Verify no category found message
    Given non admin user is logged in
    When user navigates to Categories page
    Then if categories are empty "No category found" should be displayed

  @UI_CAT_USR_003 @215564H
  Scenario: [UI_CAT_USR_003] Verify non-admin cannot edit or delete categories
    Given non admin user is logged in
    When user navigates to Categories page
    Then category edit and delete buttons should not be clickable

  # -----------------------------------------------------------------------
  # 215552U – Unauthenticated access
  # -----------------------------------------------------------------------

  @UI_CAT_USR_004 @215552U
  Scenario: [UI_CAT_USR_004] Unauthenticated user accessing categories is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/categories"
    Then I should be redirected to the login page
