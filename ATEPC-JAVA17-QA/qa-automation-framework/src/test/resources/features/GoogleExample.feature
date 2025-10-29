@AllExamples
Feature: Example
  This feature is an example
  
	@Example1	
 	Scenario Outline: Example Scenario 1
	  Given I launch a <browser> browser for <test name>
	  And Google is opened 
	  Then the <page name> is displayed
	  
	  Examples:
		  | user         | browser   | test name                    | search term              | button              | page name                               |
		  | 103oneide033 | "Chrome"  | "Log4j2 Test Headless"      | "Raytheon"               | "I'm Feeling Lucky" | "Google"         |
#		  | 103oneide030 | "Chrome"  | "Example Test 2"             | "United Technologies"    | "I'm Feeling Lucky" | "Home \| Raytheon Technologies"         |
#		  | 103oneide025 | "Chrome"  | "Example Test 3 - WILL FAIL" | "United Technologies"    | "Search"            | "Home \| Raytheon Technologies"         |
#		  | 103oneide028 | "Chrome"  | "Example Test 4"             | "United Technologies"    | "Search"            | "United Technologies - Google Search"   |

	@UserTest
	Scenario Outline: Homepage Scenario 1
	  Given <userID> launches a <browser> browser for auth <test name> 
		Then Verify Authpage is opened for <user name>
		Then Verify Authpage opened for <user name>
		  
	  Examples:
		  | userID         | browser | test name   			| user name									|
		  | "103oneide033" | "Chrome"| "Example Test 1" | "User: 033 OneIDE 2007"		|
		  | "nrponeide002" | "Chrome"| "Example Test 2" | "User: Saritha R Chilkuri"|  
		