@API
Feature: Plants API Tests

  @API_GET_CAT_USR_002 @215552U
  Scenario: User can filter plants by category
    Given I am authenticated as user for plants
    When I get plants for category id 5
    Then the plants response status should be 200
    And all returned plants should belong to category 5

  @API_GET_PLT_USR_004
  Scenario: User can sort plants by price ascending
    Given I am authenticated as user for plants
    When I get plants sorted by price ascending
    Then the plants response status should be 200
    And the plants should be ordered by price ascending