@API
Feature: Sales API Tests

  @API_POST_SLS_ADM_001 @215552U
  Scenario: Admin creates a sale and plant stock is reduced
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 2
    Then the API response status should be 201
    And the plant stock should be reduced by 2

  @API_POST_SLS_ADM_002 @215552U
  Scenario: Creating a sale exceeding available stock returns an error
    Given I am authenticated as admin via API
    And I use the existing plant with id 4
    When I create a sale for that plant with quantity 999
    Then the API response status should be 400 or 422
    And the plant stock should remain unchanged

  @API_POST_SLS_ADM_003 @215552U
  Scenario: Creating a sale with quantity zero is rejected
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 0
    Then the API response status should be 400
    And the plant stock should remain unchanged

  @API_DEL_SLS_ADM_004 @215552U
  Scenario: Admin deletes a sale successfully
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    When I delete that sale via API
    Then the API response status should be 204

  @API_GET_SLS_ADM_005 @215552U
  Scenario: Admin can get the sales list sorted by date descending
    Given I am authenticated as admin via API
    When I get the sales list sorted by date descending
    Then the API response status should be 200
    And the response should contain a list of sales
