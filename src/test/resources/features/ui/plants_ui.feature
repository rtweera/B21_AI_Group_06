@UI
Feature: Plants UI Tests

  # -----------------------------------------------------------------------
  # 215565L – Admin plant management
  # -----------------------------------------------------------------------

  @UI_PLT_ADM_001 @215565L
  Scenario: UI_PLT_ADM_001 - Verify that admin users should be able to add a new plant with valid data
    Given I am logged in as admin
    And I navigate to the Plants page
    When I click the "Add a Plant" button
    And I enter plant name "Aloe Blue Elf", category "Spider Aloe", price "10.00" and quantity "10"
    And I click the Save button
    Then I should be redirected to the plants page "/ui/plants"
    And I should see a success message "Plant added successfully"

  @UI_PLT_ADM_002 @215565L
  Scenario: UI_PLT_ADM_002 - Verify that admin users get redirected to plants page when click cancel at add plant page
    Given I am logged in as admin
    And I navigate to the Plants page
    When I click the "Add a Plant" button
    And I click the Cancel button
    Then I should be redirected to the plants page "/ui/plants"

  @UI_PLT_ADM_003 @215565L
  Scenario: UI_PLT_ADM_003 - Verify that category in plants page is a subcategory
    Given I am logged in as admin
    And I navigate to the Plants page
    When I click the "Add a Plant" button
    Then the category dropdown should contain only subcategories

  @UI_PLT_ADM_004 @215565L
  Scenario: UI_PLT_ADM_004 - Verify that admin users can't add a plant with zero price
    Given I am logged in as admin
    And I navigate to the Plants page
    When I click the "Add a Plant" button
    And I enter plant name "Aloe Blue Elf 2", category "Spider Aloe", price "0.00" and quantity "10"
    And I click the Save button
    Then I should see a validation error "Price must be greater than 0"
    And I should remain on the add plant page

  @UI_PLT_ADM_005 @215565L
  Scenario: UI_PLT_ADM_005 - Verify that admin user can't delete a plant that has a sales record
    Given I am logged in as admin
    And I navigate to the Plants page
    When I click on delete icon on "Test Plant"
    Then the system should prevent deletion and display an appropriate message indicating that the plant cannot be deleted

  # -----------------------------------------------------------------------
  # 215564H – Non-admin plant access
  # -----------------------------------------------------------------------

  @UI_PLT_USR_001 @215564H
  Scenario: UI_PLT_USR_001 - Verify non-admin can view plant list
    Given non admin user is logged in
    When user navigates to Plants page
    Then plant list should be displayed as a table

  @UI_PLT_USR_002 @215564H
  Scenario: UI_PLT_USR_002 - Verify non-admin does not see Add Plant button
    Given non admin user is logged in
    When user navigates to Plants page
    Then Add Plant button should not be visible

  @UI_PLT_USR_003 @215564H
  Scenario: UI_PLT_USR_003 - Verify non-admin can search a relevant plant
    Given non admin user is logged in
    When user navigates to Plants page
    And user searches plant "plant"
    Then searched plant "plant" should be displayed

  @UI_PLT_USR_003 @215564H
  Scenario: UI_PLT_USR_003 - Verify non-admin can search a relevant plant
    Given non admin user is logged in
    When user navigates to Plants page
    And user searches plant "plant 2"
    Then searched plant "plant 2" should be displayed

  # -----------------------------------------------------------------------
  # 215565L – Non-admin plant and sales page access restrictions
  # -----------------------------------------------------------------------

  @UI_PLT_USR_004 @215565L
  Scenario: UI_PLT_USR_004 - Verify that user blocked from Add Plant page
    Given I am logged in as a non-admin user
    And I navigate to the Plants page
    Then I should not see the "Add a Plant" button
    When I navigate to the page "/ui/plants/add"
    Then I should see an Access Denied message

  @UI_PLT_USR_005 @215565L
  Scenario: UI_PLT_USR_005 - Verify that user can view the sales page
    Given I am logged in as a non-admin user
    When I navigate to the page "/ui/sales"
    Then the sales list page should display a table with at least one record

  @UI_PLT_USR_006 @215565L
  Scenario: UI_PLT_USR_006 - Verify that user does not see the Sell Plant button
    Given I am logged in as a non-admin user
    When I navigate to the page "/ui/sales"
    Then I should not see the "Sell Plant" button

  @UI_PLT_USR_007 @215565L
  Scenario: UI_PLT_USR_007 - Verify that user does not see the delete button on sales rows
    Given I am logged in as a non-admin user
    When I navigate to the page "/ui/sales"
    Then I should not see delete buttons on sales table rows

  @UI_PLT_USR_008 @215565L
  Scenario: UI_PLT_USR_008 - Verify that empty sales list displays a message
    Given I am logged in as a non-admin user
    And no sales records exist in the system
    When I navigate to the page "/ui/sales"
    Then I should see the empty sales message "No sales found"

  # -----------------------------------------------------------------------
  # 215552U – Unauthenticated access and sorting
  # -----------------------------------------------------------------------

  @UI_PLT_USR_009 @215552U
  Scenario: UI_PLT_USR_009 - Unauthenticated user accessing plants is redirected to login
    Given I am not logged in
    When I navigate to the page "/ui/plants"
    Then I should be redirected to the login page

  @UI_PLT_USR_010 @215552U
  Scenario: UI_PLT_USR_010 - User can sort the plant list by price
    Given I am logged in as user
    When I navigate to the plants page
    And I click the Price column header
    Then the plant list order should change
