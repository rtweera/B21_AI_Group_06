@UI
Feature: Authentication UI Tests

  # -----------------------------------------------------------------------
  # 215527A – Admin login and dashboard
  # -----------------------------------------------------------------------

  @UI_AUTH_ADM_001 @215527A
  Scenario: UI_AUTH_ADM_001 - Admin can log in with correct credentials
    Given I am on the login page
    When I log in with username "admin" and password "admin123"
    Then I should see the dashboard navigation

  @UI_DASH_ADM_001 @215527A
  Scenario: UI_DASH_ADM_001 - Admin dashboard shows the required summary cards only
    Given I am logged in as an admin user
    Then the dashboard should show only the categories, plants, and sales summary cards with counts

  # -----------------------------------------------------------------------
  # 215527A – Normal user login
  # -----------------------------------------------------------------------

  @UI_AUTH_USR_001 @215527A
  Scenario: UI_AUTH_USR_001 - Normal user can log in with correct credentials
    Given I am on the login page
    When I log in with username "testuser" and password "test123"
    Then I should see the dashboard navigation

  @UI_AUTH_USR_002 @215527A
  Scenario: UI_AUTH_USR_002 - Normal user login with incorrect password is prohibited
    Given I am on the login page
    When I log in with username "testuser" and password "wrongpassword"
    Then I should see the login error "Invalid username or password."

  @UI_AUTH_USR_003 @215527A
  Scenario: UI_AUTH_USR_003 - Normal user login with empty password is prohibited
    Given I am on the login page
    When I attempt login with username "testuser" and an empty password
    Then I should see the password validation message "Password is required" in red

  @UI_AUTH_USR_004 @215527A
  Scenario: UI_AUTH_USR_004 - Login with empty username and non-empty password is prohibited
    Given I am on the login page
    When I attempt login with an empty username and password "test123"
    Then I should see the username validation message "Username is required" in red

  # -----------------------------------------------------------------------
  # 215552U – Logout
  # -----------------------------------------------------------------------

  @UI_AUTH_USR_005 @215552U
  Scenario: UI_AUTH_USR_005 - User logout ends the session
    Given I am logged in as user
    When I click the Logout button
    Then I should see a logout success message
    And I should be redirected to the login page
