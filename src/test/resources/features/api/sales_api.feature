@API
Feature: Sales API Tests

  # -----------------------------------------------------------------------
  # 215552U – Admin sales management
  # -----------------------------------------------------------------------

  @API_POST_SLS_ADM_001 @215552U
  Scenario: API_POST_SLS_ADM_001 - Admin creates a sale and plant stock is reduced
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 2
    Then the API response status should be 201
    And the plant stock should be reduced by 2

  @API_POST_SLS_ADM_002 @215552U
  Scenario: API_POST_SLS_ADM_002 - Creating a sale exceeding available stock returns an error
    Given I am authenticated as admin via API
    And I use the existing plant with id 4
    When I create a sale for that plant with quantity 999
    Then the API response status should be 400 or 422
    And the plant stock should remain unchanged

  @API_POST_SLS_ADM_003 @215552U
  Scenario: API_POST_SLS_ADM_003 - Creating a sale with quantity zero is rejected
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    When I create a sale for that plant with quantity 0
    Then the API response status should be 400
    And the plant stock should remain unchanged

  @API_DEL_SLS_ADM_004 @215552U
  Scenario: API_DEL_SLS_ADM_004 - Admin deletes a sale successfully
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    When I delete that sale via API
    Then the API response status should be 204

  @API_GET_SLS_ADM_005 @215552U
  Scenario: API_GET_SLS_ADM_005 - Admin can get the sales list sorted by date descending
    Given I am authenticated as admin via API
    When I get the sales list sorted by date descending
    Then the API response status should be 200
    And the response should contain a list of sales

  # -----------------------------------------------------------------------
  # 215565L – Normal user sale access
  # -----------------------------------------------------------------------

  @API_GET_SLS_USR_001 @215565L
  Scenario: API_GET_SLS_USR_001 - Normal user can get a sale by id
    Given a plant exists with its id captured
    And a sale exists for the captured plant id with its id captured
    And I have a normal user API token
    When I request the captured sale by id
    Then the API response status should be 200
    And the API response body should contain the captured sale id

  @API_POST_SLS_USR_002 @215565L
  Scenario: API_POST_SLS_USR_002 - Normal user cannot create a sale
    Given a plant exists with its id captured
    And I have a normal user API token
    When I attempt to sell the captured plant with quantity 10
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  # -----------------------------------------------------------------------
  # 215552U – Non-admin sale deletion (bug: app returns 204 instead of 403)
  # -----------------------------------------------------------------------

  @API_DEL_SLS_USR_001 @215552U
  Scenario: API_DEL_SLS_USR_001 - Normal user cannot delete a sale and receives 403
    Given I am authenticated as admin via API
    And I use the existing plant with id 2
    And I have created a sale for that plant with quantity 1
    Given I am authenticated as user via API
    When I delete that sale via API
    Then the API response status should be 403
