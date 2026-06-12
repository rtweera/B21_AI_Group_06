@API @215552U
Feature: Authentication API Tests

  # -----------------------------------------------------------------------
  # Unauthenticated access (GET /api/categories with no token)
  # -----------------------------------------------------------------------

  @API_GET_AUTH_USR_001
  Scenario: API_GET_AUTH_USR_001 - Calling a protected endpoint without authentication returns 401
    Given I have no authentication token
    When I get categories without authentication
    Then the unauthenticated response status should be 401