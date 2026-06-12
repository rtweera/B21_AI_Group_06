@API
Feature: Categories API Tests

  # -----------------------------------------------------------------------
  # 215527A – Admin category management
  # -----------------------------------------------------------------------

  @API_GET_CAT_ADM_001 @215527A
  Scenario: API_GET_CAT_ADM_001 - Admin can get all categories
    Given I have an admin API token
    When I request all categories with the admin token
    Then the API response status should be 200
    And the API response should contain a category list

  @API_POST_CAT_ADM_001 @215527A
  Scenario: API_POST_CAT_ADM_001 - Admin can create a category
    Given I have an admin API token
    When I create a unique category with the admin token
    Then the API response status should be 201
    And the API response should contain the created category id

  @API_GET_CAT_ADM_002 @215527A
  Scenario: API_GET_CAT_ADM_002 - Admin can get a category by ID
    Given I have an admin API token
    And an admin-created category exists
    When I request that category by id with the admin token
    Then the API response status should be 200
    And the API response should contain the requested category

  @API_POST_CAT_ADM_002 @215527A
  Scenario: API_POST_CAT_ADM_002 - Admin cannot create a category with a missing name
    Given I have an admin API token
    When I create a category with a missing name using the admin token
    Then the API response status should be 400
    And the API response should contain a validation error

  # -----------------------------------------------------------------------
  # 215564H – Admin category update, delete, and name validation
  # -----------------------------------------------------------------------

  @API_PUT_CAT_ADM_001 @215564H
  Scenario: API_PUT_CAT_ADM_001 - Verify admin can update category
    Given admin API token is available
    When admin sends PUT request to update category
    Then API response status should be 200
    And API response should contain category name "UpdatedCat"

  @API_DEL_CAT_ADM_001 @215564H
  Scenario: API_DEL_CAT_ADM_001 - Verify admin can delete category
    Given admin API token is available
    When admin deletes a category
    Then API response status should be 204
    And deleted category should not be found

  @API_POST_CAT_ADM_003 @215564H
  Scenario: API_POST_CAT_ADM_003 - Verify admin cannot create category name less than 3 characters
    Given admin API token is available
    When admin creates category with name "XX"
    Then API response status should be 400
    And API response should contain message "Category name must be between 3 and 10 characters"

  @API_POST_CAT_ADM_004 @215564H
  Scenario: API_POST_CAT_ADM_004 - Verify admin cannot create category name more than 10 characters
    Given admin API token is available
    When admin creates category with name "XXXXXXXXXXX"
    Then API response status should be 400
    And API response should contain message "Category name must be between 3 and 10 characters"

  # -----------------------------------------------------------------------
  # 215527A – Normal user category access
  # -----------------------------------------------------------------------

  @API_GET_CAT_USR_001 @215527A
  Scenario: API_GET_CAT_USR_001 - Normal user can get all categories
    Given I have a normal user API token
    When I request all categories with the normal user token
    Then the API response status should be 200
    And the API response should contain a category list

  @API_POST_CAT_USR_001 @215527A
  Scenario: API_POST_CAT_USR_001 - Normal user cannot create a category
    Given I have a normal user API token
    When I create a category with the normal user token
    Then the API response status should be 403
    And the API response should contain a forbidden error

  # -----------------------------------------------------------------------
  # 215564H – Non-admin category restrictions
  # -----------------------------------------------------------------------

  @API_PUT_CAT_USR_001 @215564H
  Scenario: API_PUT_CAT_USR_001 - Verify non-admin cannot update category
    Given non admin API token is available
    When non admin user tries to update a category
    Then API response status should be 403

  @API_DEL_CAT_USR_001 @215564H
  Scenario: API_DEL_CAT_USR_001 - Verify non-admin cannot delete category
    Given non admin API token is available
    When non admin user tries to delete a category
    Then API response status should be 403

  # -----------------------------------------------------------------------
  # 215552U – Pagination
  # -----------------------------------------------------------------------

  @API_GET_CAT_USR_002 @215552U
  Scenario: API_GET_CAT_USR_002 - User can get categories with pagination
    Given I am authenticated as user for categories
    When I get categories with page 0 and size 5
    Then the categories response status should be 200
    And the response should contain at most 5 categories
