Yellowfire Dispatcher Jolokia Plugin
====================================

Introduction
------------
For this plugin to work Jolokia must be installed either via JVM agent or if the exec functionality is required then as a WAR. If the latter option is chosen then the security of the web application needs to be configured to use the Weblogic Administrators group.


How to use
----------

```scala
import dispatch._
import Defaults._
import jolokia.request._
/* Working on making the test package the response package*/
import jolokia.test._
import org.json4s._

val client = new AbstractClient("localhost", 7001)
val result = http(client(Search("com.bea:*,Type=JMSDestinationRuntime")) OK as.json4s.Json)
val json = result()

/* Working on the ObjectName case class to make it more widely used in the library */
val response = json.extract[SearchResponse]

val timeout = response.timeout
val status = response.status
val request = response.request
val objectNames = response.value

objectNames.foreach { objectName => println(objectName) }
objectNames.foreach { objectName => println(objectName.domain) }
objectNames.foreach { objectName => println(objectName.properties) }
```

