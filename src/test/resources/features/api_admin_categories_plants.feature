@API @215564H
Feature: Admin API category and plant management
  Background:
    Given admin API token is available

  @API @Admin @215564H
  Scenario: API_PUT_CAT_ADM_001 Verify admin can update category
    When admin sends PUT request to update category
    Then API response status should be 200
    And API response should contain category name "UpdatedCat"

  @API @Admin @215564H
  Scenario: API_DEL_CAT_ADM_001 Verify admin can delete category
    When admin deletes a category
    Then API response status should be 204
    And deleted category should not be found

  @API @Admin @215564H
  Scenario: API_POST_CAT_ADM_003 Verify admin cannot create category name less than 3 characters
    When admin creates category with name "XX"
    Then API response status should be 400
    And API response should contain message "Category name must be between 3 and 10 characters"

  @API @Admin @215564H
  Scenario: API_POST_CAT_ADM_004 Verify admin cannot create category name more than 10 characters
    When admin creates category with name "XXXXXXXXXXX"
    Then API response status should be 400
    And API response should contain message "Category name must be between 3 and 10 characters"

  @API @Admin @215564H
  Scenario: API_GET_PLT_ADM_001 Verify admin can view all plants
    When admin gets all plants
    Then API response status should be 200