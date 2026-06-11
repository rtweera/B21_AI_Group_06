@api @215527A
Feature: API category access

  @API_GET_CAT_ADM_001
  Scenario: API_GET_CAT_ADM_001 - Admin can get all categories
    Given I have an admin API token
    When I request all categories with the admin token
    Then the API response status should be 200
    And the API response should contain a category list

  @API_POST_CAT_ADM_001
  Scenario: API_POST_CAT_ADM_001 - Admin can create a category
    Given I have an admin API token
    When I create a unique category with the admin token
    Then the API response status should be 201
    And the API response should contain the created category id

  @API_GET_CAT_ADM_002
  Scenario: API_GET_CAT_ADM_002 - Admin can get a category by ID
    Given I have an admin API token
    And an admin-created category exists
    When I request that category by id with the admin token
    Then the API response status should be 200
    And the API response should contain the requested category

  @API_POST_CAT_ADM_002
  Scenario: API_POST_CAT_ADM_002 - Admin cannot create a category with a missing name
    Given I have an admin API token
    When I create a category with a missing name using the admin token
    Then the API response status should be 400
    And the API response should contain a validation error

  @API_GET_CAT_USR_001
  Scenario: API_GET_CAT_USR_001 - Normal user can get all categories
    Given I have a normal user API token
    When I request all categories with the normal user token
    Then the API response status should be 200
    And the API response should contain a category list

  @API_POST_CAT_USR_001
  Scenario: API_POST_CAT_USR_001 - Normal user cannot create a category
    Given I have a normal user API token
    When I create a category with the normal user token
    Then the API response status should be 403
    And the API response should contain a forbidden error
