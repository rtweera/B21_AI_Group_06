@API @215564H
Feature: non admin API category and plant management
  Background:
    Given non admin API token is available

  @API @User @215564H
  Scenario: API_PUT_CAT_USR_001 Verify non-admin cannot update category
    When non admin user tries to update a category
    Then API response status should be 403

  @API @User @215564H
  Scenario: API_DEL_CAT_USR_001 Verify non-admin cannot delete category
    When non admin user tries to delete a category
    Then API response status should be 403

  @API @User @215564H
  Scenario: API_GET_PLT_USR_001 Verify non-admin can see all plants
    When non admin user gets all plants
    Then API response status should be 200

  @API @User @215564H
  Scenario: API_GET_PLT_USR_002 Verify non-admin can get plant by ID
    When non admin user gets a plant by ID
    Then API response status should be 200

  @API @User @215564H
  Scenario: API_POST_PLT_USR_001 Verify non-admin cannot create plant
    When non admin user tries to create a plant
    Then API response status should be 403