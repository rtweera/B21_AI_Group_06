@API @215552U
Feature: Plants API Tests

  # -----------------------------------------------------------------------
  # Normal user – Filter plants by category (GET /api/plants/category/{id})
  # -----------------------------------------------------------------------

  @API_GET_PLT_USR_004
  Scenario: API_GET_CAT_USR_004 - User can filter plants by category
    Given I am authenticated as user for plants
    When I get plants for category id 5
    Then the plants response status should be 200
    And all returned plants should belong to category 5

  # -----------------------------------------------------------------------
  # Normal user – Sort plants by price (GET /api/plants/paged?sort=price,asc)
  # -----------------------------------------------------------------------

  @API_GET_PLT_USR_005
  Scenario: API_GET_PLT_USR_005 - User can sort plants by price ascending
    Given I am authenticated as user for plants
    When I get plants sorted by price ascending
    Then the plants response status should be 200
    And the plants should be ordered by price ascending