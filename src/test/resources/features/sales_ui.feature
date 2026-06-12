@UI @215552U
Feature: Sales UI Tests

  @UI_SLS_ADM_001
  Scenario: Admin sells a plant successfully
    Given I am logged in as admin
    And a plant is available with stock greater than 0
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "2"
    And I click the Sell button
    Then I should be redirected to the sales list page
    And a new sale record should be visible in the sales table

  @UI_SLS_ADM_002
  Scenario: Selling a plant with quantity zero shows an error
    Given I am logged in as admin
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "0"
    And I click the Sell button
    Then I should see an error message about quantity
    And I should remain on the Sell Plant page

  @UI_SLS_ADM_003
  Scenario: Admin deletes a sales record successfully
    Given I am logged in as admin
    And at least one sale exists in the system
    When I navigate to the sales list page
    And I delete the first sale and confirm
    Then the sale record should be removed from the list
    And I should remain on the sales list page

  @UI_SLS_ADM_004
  Scenario: Sales list is sorted by date descending by default
    Given I am logged in as admin
    And at least two sales exist in the system
    When I navigate to the sales list page
    Then the most recent sale should appear first

  @UI_SLS_ADM_005
  Scenario: Cancel on Sell Plant page navigates back to sales list
    Given I am logged in as admin
    When I navigate to the Sell Plant page
    And I click the Cancel button
    Then I should be redirected to the sales list page

  @UI_SLS_USR_001
  Scenario: Unauthenticated user accessing categories is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/categories"
    Then I should be redirected to the login page

  @UI_SLS_USR_002
  Scenario: Unauthenticated user accessing plants is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/plants"
    Then I should be redirected to the login page

  @UI_SLS_USR_003
  Scenario: Unauthenticated user accessing sales is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/sales"
    Then I should be redirected to the login page