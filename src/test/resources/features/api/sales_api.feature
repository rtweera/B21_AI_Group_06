@API @215552U
Feature: Sales API Tests

  # -----------------------------------------------------------------------
  # Admin – Create sale (POST /api/sales/plant/{plantId}?quantity=…)
  # -----------------------------------------------------------------------

  @API_POST_SLS_ADM_001
  Scenario: API_POST_SLS_ADM_001 - Admin creates a sale and plant stock is reduced
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 2
    Then the API response status should be 201
    And the plant stock should be reduced by 2

  @API_POST_SLS_ADM_002
  Scenario: API_POST_SLS_ADM_002 - Creating a sale exceeding available stock returns an error
    Given I am authenticated as admin via API
    And I use the existing plant with id 4
    When I create a sale for that plant with quantity 999
    Then the API response status should be 400 or 422
    And the plant stock should remain unchanged

  @API_POST_SLS_ADM_003
  Scenario: API_POST_SLS_ADM_003 - Creating a sale with quantity zero is rejected
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 0
    Then the API response status should be 400
    And the plant stock should remain unchanged

  # -----------------------------------------------------------------------
  # Admin – Delete sale (DELETE /api/sales/{saleId})
  # -----------------------------------------------------------------------

  @API_DEL_SLS_ADM_004
  Scenario: API_DEL_SLS_ADM_004 - Admin deletes a sale successfully
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    When I delete that sale via API
    Then the API response status should be 204

  # -----------------------------------------------------------------------
  # Admin – Get sales with sorting (GET /api/sales/page?sort=soldAt,desc)
  # -----------------------------------------------------------------------

  @API_GET_SLS_ADM_005
  Scenario: API_GET_SLS_ADM_005 - Admin can get the sales list sorted by date descending
    Given I am authenticated as admin via API
    When I get the sales list sorted by date descending
    Then the API response status should be 200
    And the response should contain a list of sales

  # -----------------------------------------------------------------------
  # Normal user – Delete sale (should be 403, but returns 204 — BUG-SLS-001)
  # -----------------------------------------------------------------------

  @API_DEL_SLS_USR_001
  Scenario: API_DEL_SLS_USR_001 - Normal user cannot delete a sale and receives 403
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    Given I am authenticated as user via API
    When I delete that sale via API
    Then the API response status should be 403