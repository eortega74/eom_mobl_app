Feature: API Example Test

    #To update the table values for the following Given, use api.properties
    @API_Example
		Scenario Outline: Validate HTTP Response
			Given <user> launches <browser> browser to obtain SMSESSION cookie from <cookieSite> for endpoint <endPoint>
			When cookie is passed in the header
			Then user can validate the HTTP response code

    	Examples: 
     		| user   			| browser  | cookieSite 						| endPoint 	 |
     		| "test.user" | "chrome" | "obtain.SMScookie.url" | "endPoint" |
     		
    @JWT_Example
		Scenario Outline: Generate JWTToken
		Given I Set GET SOA service api <endpoint>

		Examples: 
     		| endpoint   			     				|
     		| "nonProd.echo-jwt.endpoint" |
     		|	"Prod.echo-jwt.endpoint"		|
 