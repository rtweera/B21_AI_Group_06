@API
Feature: Categories API Tests

  @API_GET_CAT_USR_003 @215552U
  Scenario: User can get categories with pagination
    Given I am authenticated as user for categories
    When I get categories with page 0 and size 5
    Then the categories response status should be 200
    And the response should contain at most 5 categories