package jolokia.request.test

import org.scalatest.FlatSpec
import jolokia.request._

class RequestSpec extends FlatSpec {
  
  "A client" should "be able to retrieve the JMS destinations from Weblogic" in {
	  import jolokia.response._
	  import dispatch._
	  import Defaults._
	  
	  val http = new Http
	  val client = new Client("localhost", 7001)
	  
	  val response = client.search("com.bea:*,Type=JMSDestinationRuntime")
	  assert(response.status == 200)
  }
}