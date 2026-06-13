@API
Feature: Plants API Tests

  # -----------------------------------------------------------------------
  # 215565L – Admin plant management
  # -----------------------------------------------------------------------

  @API_POST_PLT_ADM_001 @215565L
  Scenario: [API_POST_PLT_ADM_001] Admin creates a valid plant under a sub-category
    Given I have an admin API token
    When I create a unique plant under category 5 with price 150.0 and quantity 25
    Then the API response status should be 201
    And the API response body should contain the created plant name
    And the API response body should contain price 150.0
    And the API response body should contain quantity 25
    And the API response body should contain category id 5

  @API_POST_PLT_ADM_002 @215565L
  Scenario: [API_POST_PLT_ADM_002] Admin cannot create a duplicate plant in the same category
    Given I have an admin API token
    And a plant named "TestAloe" exists under category 5
    When I create a plant named "TestAloe" under category 5 with price 150.0 and quantity 25
    Then the API response status should be 400
    And the API response body should indicate a bad request error

  @API_POST_PLT_ADM_003 @215565L
  Scenario: [API_POST_PLT_ADM_003] Admin cannot create a plant with negative quantity
    Given I have an admin API token
    When I create a unique plant under category 5 with price 150.0 and quantity -1
    Then the API response status should be 400
    And the API response body should indicate a bad request error

  @API_PUT_PLT_ADM_004 @215565L
  Scenario: [API_PUT_PLT_ADM_004] Admin updating a plant with a non-existent id returns 404
    Given I have an admin API token
    When I send a PUT request for plant id 99999 with name "Ghost" price 50.0 and quantity 5
    Then the API response status should be 404
    And the API response body should indicate a not found error

  @API_DEL_PLT_ADM_005 @215565L
  Scenario: [API_DEL_PLT_ADM_005] Admin can delete an existing plant
    Given I have an admin API token
    And a plant exists with its id captured
    When I send a DELETE request for the captured plant id
    Then the API response status should be 204
    And a GET request for the captured plant id should return 404

  # -----------------------------------------------------------------------
  # 215564H – Admin plant read
  # -----------------------------------------------------------------------

  @API_GET_PLT_ADM_001 @215564H
  Scenario: [API_GET_PLT_ADM_001] Verify admin can view all plants
    Given admin API token is available
    When admin gets all plants
    Then API response status should be 200

  # -----------------------------------------------------------------------
  # 215565L – Normal user plant access
  # -----------------------------------------------------------------------

  @API_PUT_PLT_USR_001 @215565L
  Scenario: [API_PUT_PLT_USR_001] Normal user cannot update a plant
    Given a plant exists with its id captured
    And I have a normal user API token
    When I send a PUT request for the captured plant id with name "Updated" price 99.0 and quantity 10
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  @API_DEL_PLT_USR_002 @215565L
  Scenario: [API_DEL_PLT_USR_002] Normal user cannot delete a plant
    Given a plant exists with its id captured
    And I have a normal user API token
    When I send a DELETE request for the captured plant id
    Then the API response status should be 403
    And the API response body should indicate a forbidden error

  @API_GET_PLT_USR_003 @215565L
  Scenario: [API_GET_PLT_USR_003] Normal user can retrieve all plants
    Given I have a normal user API token
    When I request all plants
    Then the API response status should be 200
    And the API response body should contain a plant list

  # -----------------------------------------------------------------------
  # 215564H – Non-admin plant access
  # -----------------------------------------------------------------------

  @API_GET_PLT_USR_001 @215564H
  Scenario: [API_GET_PLT_USR_001] Verify non-admin can see all plants
    Given non admin API token is available
    When non admin user gets all plants
    Then API response status should be 200

  @API_GET_PLT_USR_002 @215564H
  Scenario: [API_GET_PLT_USR_002] Verify non-admin can get plant by ID
    Given non admin API token is available
    When non admin user gets a plant by ID
    Then API response status should be 200

  @API_POST_PLT_USR_001 @215564H
  Scenario: [API_POST_PLT_USR_001] Verify non-admin cannot create plant
    Given non admin API token is available
    When non admin user tries to create a plant
    Then API response status should be 403

  # -----------------------------------------------------------------------
  # 215552U – Filter and sort
  # -----------------------------------------------------------------------

  @API_GET_PLT_USR_004 @215552U
  Scenario: [API_GET_PLT_USR_004] User can filter plants by category
    Given I am authenticated as user for plants
    When I get plants for category id 5
    Then the plants response status should be 200
    And all returned plants should belong to category 5

  @API_GET_PLT_USR_005 @215552U
  Scenario: [API_GET_PLT_USR_005] User can sort plants by price ascending
    Given I am authenticated as user for plants
    When I get plants sorted by price ascending
    Then the plants response status should be 200
    And the plants should be ordered by price ascending

  # -----------------------------------------------------------------------
  # 215565L – Bug / contract compliance
  # -----------------------------------------------------------------------

  @API_POST_PLT_ADM_006 @215565L
  Scenario: [API_POST_PLT_ADM_006] Backend must accept the exact Swagger example body and return 201 (contract compliance)
    Given I have an admin API token
    When I create a plant using the full Swagger example body under category 5
    Then the API response status should be 201

  @API_POST_PLT_ADM_007 @215565L
  Scenario: [API_POST_PLT_ADM_007] Backend must not return 500 when optional Swagger fields are included (error handling)
    Given I have an admin API token
    When I create a plant using the full Swagger example body under category 5
    Then the API response status should not be 500
