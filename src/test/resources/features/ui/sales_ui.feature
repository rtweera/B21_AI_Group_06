@UI
Feature: Sales UI Tests

  # -----------------------------------------------------------------------
  # 215552U – Admin sales management
  # -----------------------------------------------------------------------

  @UI_SLS_ADM_001 @215552U
  Scenario: [UI_SLS_ADM_001] Admin sells a plant successfully
    Given I am logged in as admin
    And a plant is available with stock greater than 0
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "2"
    And I click the Sell button
    Then I should be redirected to the sales list page
    And a new sale record should be visible in the sales table

  @UI_SLS_ADM_002 @215552U
  Scenario: [UI_SLS_ADM_002] Selling a plant with quantity zero shows an error
    Given I am logged in as admin
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "0"
    And I click the Sell button
    Then I should see an error message about quantity
    And I should remain on the Sell Plant page

  @UI_SLS_ADM_003 @215552U
  Scenario: [UI_SLS_ADM_003] Admin deletes a sale record successfully
    Given I am logged in as admin
    And at least one sale exists in the system
    When I navigate to the sales list page
    And I delete the first sale and confirm
    Then the sale record should be removed from the list
    And I should remain on the sales list page

  @UI_SLS_ADM_004 @215552U
  Scenario: [UI_SLS_ADM_004] Sales list is sorted by date descending by default
    Given I am logged in as admin
    And at least two sales exist in the system
    When I navigate to the sales list page
    Then the most recent sale should appear first

  @UI_SLS_ADM_005 @215552U
  Scenario: [UI_SLS_ADM_005] Cancel on Sell Plant page navigates back to sales list
    Given I am logged in as admin
    When I navigate to the Sell Plant page
    And I click the Cancel button
    Then I should be redirected to the sales list page

  # -----------------------------------------------------------------------
  # 215552U – Unauthenticated access
  # -----------------------------------------------------------------------

  @UI_SLS_USR_001 @215552U
  Scenario: [UI_SLS_USR_001] Unauthenticated user accessing sales is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/sales"
    Then I should be redirected to the login page
