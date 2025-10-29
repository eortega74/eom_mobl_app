
Feature: Database Test
  This feature is to test a database connection and simple query

Background: I want to test a database connnection
  @db1
  Scenario Outline: Run simple database query
    Given I connect to <dbName> database
    When I run a query
    Then the results are correct
    And the connection is closed
    Examples:
    | dbName	  	|
    |   "sql"			|
  
  @database  
  Scenario: Confirm database connection
    Given I connect to the database
    Then the connection is closed
    
    
    
    