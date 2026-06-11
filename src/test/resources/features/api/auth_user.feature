@API @215552U
Feature: Authentication API Tests

  @API_GET_AUTH_USR_001
  Scenario: Calling a protected endpoint without authentication returns 401
    Given I have no authentication token
    When I get categories without authentication
    Then the unauthenticated response status should be 401