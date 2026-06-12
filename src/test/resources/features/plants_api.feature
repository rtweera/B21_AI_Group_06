@API @215565L
Feature: Plants API Tests

  # -----------------------------------------------------------------------
  # Admin – Create plant (POST /api/plants/category/{categoryId})
  # -----------------------------------------------------------------------

  @API_POST_PLT_ADM_001
  Scenario: API_POST_PLT_ADM_001 - Admin creates a valid plant under a sub-category
    Given I have an admin API token
    When I create a unique plant under category 5 with price 150.0 and quantity 25
    Then the API response status should be 201
    And the API response body should contain the created plant name
    And the API response body should contain price 150.0
    And the API response body should contain quantity 25
    And the API response body should contain category id 5

  @API_POST_PLT_ADM_002
  Scenario: API_POST_PLT_ADM_002 - Admin cannot create a duplicate plant in the same category
    Given I have an admin API token
    And a plant named "TestAloe" exists under category 5
    When I create a plant named "TestAloe" under category 5 with price 150.0 and quantity 25
    Then the API response status should be 400
    And the API response body should indicate a bad request error

  @API_POST_PLT_ADM_003
  Scenario: API_POST_PLT_ADM_003 - Admin cannot create a plant with negative quantity
    Given I have an admin API token
    When I create a unique plant under category 5 with price 150.0 and quantity -1
    Then the API response status should be 400
    And the API response body should indicate a bad request error

  # -----------------------------------------------------------------------
  # Admin – Update plant (PUT /api/plants/{id})
  # -----------------------------------------------------------------------

  @API_PUT_PLT_ADM_004
  Scenario: API_PUT_PLT_ADM_004 - Admin updating a plant with a non-existent id returns 404
    Given I have an admin API token
    When I send a PUT request for plant id 99999 with name "Ghost" price 50.0 and quantity 5
    Then the API response status should be 404
    And the API response body should indicate a not found error

  # -----------------------------------------------------------------------
  # Admin – Delete plant (DELETE /api/plants/{id})
  # -----------------------------------------------------------------------

  @API_DEL_PLT_ADM_005
  Scenario: API_DEL_PLT_ADM_005 - Admin can delete an existing plant
    Given I have an admin API token
    And a plant exists with its id captured
    When I send a DELETE request for the captured plant id
    Then the API response status should be 204
    And a GET request for the captured plant id should return 404

  # -----------------------------------------------------------------------
  # Normal user – Update/Delete plant (should be 403)
  # -----------------------------------------------------------------------

  @API_PUT_PLT_USR_001
  Scenario: API_PUT_PLT_USR_001 - Normal user cannot update a plant
    Given a plant exists with its id captured
    And I have a normal user API token
    When I send a PUT request for the captured plant id with name "Updated" price 99.0 and quantity 10
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  @API_DEL_PLT_USR_002
  Scenario: API_DEL_PLT_USR_002 - Normal user cannot delete a plant
    Given a plant exists with its id captured
    And I have a normal user API token
    When I send a DELETE request for the captured plant id
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  # -----------------------------------------------------------------------
  # Normal user – Read plants (GET /api/plants)
  # -----------------------------------------------------------------------

  @API_GET_PLT_USR_003
  Scenario: API_GET_PLT_USR_003 - Normal user can retrieve all plants
    Given I have a normal user API token
    When I request all plants
    Then the API response status should be 200
    And the API response body should contain a plant list

  # -----------------------------------------------------------------------
  # Normal user – Read sale by id (GET /api/sales/{id})
  # -----------------------------------------------------------------------

  @API_GET_SLS_USR_001
  Scenario: API_GET_SLS_USR_001 - Normal user can get a sale by id
    Given a plant exists with its id captured
    And a sale exists for the captured plant id with its id captured
    And I have a normal user API token
    When I request the captured sale by id
    Then the API response status should be 200
    And the API response body should contain the captured sale id

  # -----------------------------------------------------------------------
  # Normal user – Create sale (POST /api/sales/plant/{plantId}?quantity=…)
  # -----------------------------------------------------------------------

  @API_POST_SLS_USR_002
  Scenario: API_POST_SLS_USR_002 - Normal user cannot create a sale
    Given a plant exists with its id captured
    And I have a normal user API token
    When I attempt to sell the captured plant with quantity 10
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  # -----------------------------------------------------------------------
  # Admin – Create plant with full Swagger example body (Bug test cases)
  # Two separate defects; two separate test cases.
  # -----------------------------------------------------------------------

  @API_POST_PLT_ADM_006
  Scenario: API_POST_PLT_ADM_006 - Backend must accept the exact Swagger example body and return 201 (contract compliance)
    Given I have an admin API token
    When I create a plant using the full Swagger example body under category 5
    Then the API response status should be 201

  @API_POST_PLT_ADM_007
  Scenario: API_POST_PLT_ADM_007 - Backend must not return 500 when optional Swagger fields are included (error handling)
    Given I have an admin API token
    When I create a plant using the full Swagger example body under category 5
    Then the API response status should not be 500
