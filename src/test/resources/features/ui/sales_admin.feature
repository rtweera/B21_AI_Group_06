@UI
Feature: Sales Admin UI Tests

  Background:
    Given I am logged in as admin

  @UI_SLS_ADM_001 @215552U
  Scenario: Admin sells a plant successfully
    And a plant is available with stock greater than 0
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "2"
    And I click the Sell button
    Then I should be redirected to the sales list page
    And a new sale record should be visible in the sales table

  @UI_SLS_ADM_002 @215552U
  Scenario: Selling a plant with quantity zero shows an error
    When I navigate to the Sell Plant page
    And I select the first plant from the dropdown
    And I enter quantity "0"
    And I click the Sell button
    Then I should see an error message about quantity
    And I should remain on the Sell Plant page

  @UI_SLS_ADM_003 @215552U
  Scenario: Admin deletes a sales record successfully
    And at least one sale exists in the system
    When I navigate to the sales list page
    And I delete the first sale and confirm
    Then the sale record should be removed from the list
    And I should remain on the sales list page

  @UI_SLS_ADM_004 @215552U
  Scenario: Sales list is sorted by date descending by default
    And at least two sales exist in the system
    When I navigate to the sales list page
    Then the most recent sale should appear first

  @UI_SLS_ADM_005 @215552U
  Scenario: Cancel on Sell Plant page navigates back to sales list
    When I navigate to the Sell Plant page
    And I click the Cancel button
    Then I should be redirected to the sales list page