@API @215527A
Feature: API authentication

  @API_POST_AUTH_ADM_001
  Scenario: API_POST_AUTH_ADM_001 - Admin login returns an auth token
    When I request an admin auth token
    Then the API response status should be 200
    And the API response should contain an auth token

  @API_POST_AUTH_USR_001
  Scenario: API_POST_AUTH_USR_001 - Normal user login returns an auth token
    When I request a normal user auth token
    Then the API response status should be 200
    And the API response should contain an auth token

  @API_POST_AUTH_USR_002
  Scenario: API_POST_AUTH_USR_002 - Normal user login with incorrect password fails
    When I request a normal user auth token with password "wrongpassword"
    Then the API response status should be 401
    And the API response should contain an unauthorized error

  @API_POST_AUTH_USR_003
  Scenario: API_POST_AUTH_USR_003 - Login request with an empty body fails
    When I request an auth token with an empty body
    Then the API response status should be 401
    And the API response should contain an unauthorized error
