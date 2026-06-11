@API
Feature: Sales API Tests

  @API_DEL_SLS_USR_001
  Scenario: User cannot delete a sale and receives 403
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    Given I am authenticated as user via API
    When I delete that sale via API
    Then the API response status should be 403