@API @215552U
Feature: Categories API Tests

  # -----------------------------------------------------------------------
  # Normal user – Categories pagination (GET /api/categories/page)
  # -----------------------------------------------------------------------

  @API_GET_CAT_USR_002
  Scenario: API_GET_CAT_USR_002 - User can get categories with pagination
    Given I am authenticated as user for categories
    When I get categories with page 0 and size 5
    Then the categories response status should be 200
    And the response should contain at most 5 categories