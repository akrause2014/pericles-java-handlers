Java Handlers
=============

Creates a demo HTTP server in Java to create and manage processes to be executed on the server.

Installation
------------

Requires Java version 8 and Maven.

To create the webapp run:

`$ mvn3 package`

This compiles and packages the web application as `.war` 
which can be deployed to any web server, for example Tomcat.
It also builds a jar to startup the application in standalone mode:

`$ java -cp "target/JavaHandler/WEB-INF/lib/*" eu.pericles.handler.HandlerApplication` 

Usage
-----

Create a process:

 - request:

 `curl -H "Content-Type: application/json" -d '{ "cmd" : "sleep", "params" : [20] } ' http://localhost:8080/payloads`
   
 - result:
 
`{
	"id":"d7278b78-1214-4658-8991-d6b5054102ea",
	"cmd":"sleep",
	"params":[20],
	"status":"running"}`

List all processes:   

- request:

`curl http://localhost:8080/payloads`

- response:

`[
	{
		"id":"cf7b9dba-f1e5-4c10-8cdd-fd2773a89cf3",
		"cmd":"ping",
		"params":["-c",5,"google.com"],
		"status":"running"
	},
	{
		"id":"d7278b78-1214-4658-8991-d6b5054102ea",
		"cmd":"sleep",
		"params":[20],
		"status":"completed"
	}
]`

Get a specific payload:

- request

`curl http://localhost:8080/payloads/d7278b78-1214-4658-8991-d6b5054102ea`

- response:

`{
	"id":"d7278b78-1214-4658-8991-d6b5054102ea",
	"cmd":"sleep",
	"params":[20],
	"status":
	"completed"
}`

Delete a payload:

- request:

`curl -X DELETE http://localhost:8080/payloads/cdf91340-c473-44f1-bf18-efa632345d8f`

- response: HTTP status 200 (OK)
