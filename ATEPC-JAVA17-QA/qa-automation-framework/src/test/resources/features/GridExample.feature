@AllGridExamples
Feature: Grid Example
  This feature file provides example tests for both the hUTC & hRTN grids
  Make sure to appropriately update test.executionLocation in test.properties
  
  For the hUTCExample, use url: http://sharepoint.utc.com/corp/ontrac/Pages/ontrac.aspx
  For the hRTNExample, use url: http://erponq.apex.ray.com/irj/portal/
  
  The second row of the table for the hUTCGridExample test is intentionally made to fail with an invalid page title
  
	@hUTCGridExample
	Scenario Outline: Example hUTC Scenario
		Given I launch a <browser> browser
		When the application is opened
		Then the application <page title> is correct
		
	Examples:
		| browser  | page title |
		| "Chrome" | "OnTRAC"   |
#		| "Chrome" | "OnTRACjj" |

	@hRTNGridExample
	Scenario Outline: hRTN Grid test
	  Given <userID> launches a <browser> browser for auth <test name> 
		When the application is opened
		Then the application <page title> is correct
	Examples:
		  | userID          | browser   | test name                                 | page title  																		| ExeFileName 														|
		  | "103oneide025 " | "Chrome"  | "Verify Acess to App With Valid hRTN id." | "My Workspace Overview - SAP NetWeaver Portal" 	|  "103oneide033BasicAuthWithDomain.exe"  |		
#		  | "103oneide028"  | "Chrome"  | "Verify Acess to App With Valid hRTN id." | "My Workspace Overview - SAP NetWeaver Portal" 	|  "103oneide033BasicAuthWithDomain.exe"  |	
#		  | "103sp022"      | "Chrome"  | "Verify Acess to App With Valid hRTN id." | "My Workspace Overview - SAP NetWeaver Portal" 	|  "103sp022BasicAuthWithDomain.exe"      |			  
#		  | "103oneide030 " | "Chrome"  | "Verify Acess to App With Valid hRTN id." | "My Workspace Overview - SAP NetWeaver Portal"  |  "103oneide033BasicAuthWithDomain.exe"  |			  
