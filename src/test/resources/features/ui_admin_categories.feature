Feature: Admin category management
  Background:
    Given admin user is logged in

  @UI @Admin @215564H
  Scenario: UI_CAT_ADM_004 Verify Add A Category button is clickable
    When admin navigates to Categories page
    And admin clicks Add A Category button
    Then admin should be redirected to Add Category page

  @UI @Admin @215564H
  Scenario: UI_CAT_ADM_005 Verify admin can add category name
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "TestCat"
    Then category name should be entered successfully

  @UI @Admin @215564H
  Scenario: UI_CAT_ADM_006 Verify Cancel redirects to Categories Page
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin clicks Cancel button
    Then admin should be redirected to Categories page

  @UI @Admin @215564H
  Scenario: UI_CAT_ADM_007 Verify admin can save a new category
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "TestCat"
    And admin clicks Save button
    Then saved category "TestCat" should be visible in category list

  @UI @Admin @215564H
  Scenario: UI_CAT_ADM_008 Verify admin can create sub category
    When admin navigates to Categories page
    And admin clicks Add A Category button
    And admin enters category name "SubCat"
    And admin selects parent category "TestCat"
    And admin clicks Save button
    Then sub category "SubCat" should be visible with parent category "TestCat"