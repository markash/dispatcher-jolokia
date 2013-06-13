package jolokia.weblogic.test


import org.scalatest.FlatSpec
import jolokia.weblogic.WeblogicClient
import jolokia.weblogic.JMSDestinationRuntime

class WeblogicSpec extends FlatSpec {
    import jolokia.response._
    import jolokia.weblogic._
	import java.text.SimpleDateFormat
	
	val readResponse: ReadResponse = 
	  new ReadResponse(
	      new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2013-05-29 14:39:23"),
	      200,
	      Request("com.bea:JMSServerRuntime=jms-server,Name=jms-module!yellowfire.jms.queue.in.accounting,ServerRuntime=testserver,Type=JMSDestinationRuntime","read"),
	      Map[String, Any](
	          "BytesHighCount" -> BigInt(5943), 
	          "Name" -> "jms-module!yellowfire.jms.queue.in.accounting", 
	          "MessagesHighCount" -> BigInt(7), 
	          "MessagesThresholdTime" -> BigInt(0), 
	          "ConsumptionPaused" -> false, 
	          "Parent" -> Map("objectName" -> "com.bea:Name=jms-server,ServerRuntime=testserver,Type=JMSServerRuntime"), 
	          "MessagesDeletedCurrentCount" -> BigInt(0), 
	          "BytesPendingCount" -> BigInt(100), 
	          "MessagesReceivedCount" -> BigInt(74), 
	          "DurableSubscribers" -> null, 
	          "ConsumptionPausedState" -> "Consumption-Enabled", 
	          "BytesThresholdTime" -> BigInt(0), 
	          "ProductionPausedState" -> "Production-Enabled", 
	          "ConsumersHighCount" -> BigInt(2), 
	          "MessagesMovedCurrentCount" -> BigInt(0), 
	          "ConsumersCurrentCount" -> BigInt(1), 
	          "Paused" -> false, 
	          "DestinationType" -> "Queue", 
	          "InsertionPaused" -> false, 
	          "DestinationInfo" -> Map("SerializedDestination" -> "rO0ABXNyACN3ZWJsb2dpYy5qbXMuY29tbW9uLkRlc3RpbmF0aW9uSW1wbFSmyJ1qZfv8DAAAeHB3mLZBACZqbXMtbW9kdWxlIWVjcy5qbXMucXVldWUuaW4uYWNjb3VudGluZwAKam1zLXNlcnZlcgAKam1zLW1vZHVsZQEAA0FsbAICXfZhPvAW/AYAAAAKAQAKdGVzdHNlcnZlcmT25u4CXfZhPvAW/AYAAAATAQAKdGVzdHNlcnZlcmT25u4AAQAPX1dMU190ZXN0c2VydmVyeA==", 
	              "Name" -> "jms-module!yellowfire.jms.queue.in.accounting", 
	              "Topic" -> false, 
	              "ServerName" -> "jms-server", 
	              "VersionNumber" -> BigInt(1), 
	              "Queue" -> true, 
	              "ApplicationName" -> "jms-module", 
	              "ModuleName" -> null), 
              "InsertionPausedState" -> "Insertion-Enabled", 
              "JMSDurableSubscriberRuntimes" -> null, 
              "BytesReceivedCount" -> BigInt(51655), 
              "ConsumersTotalCount" -> BigInt(15), 
              "ProductionPaused" -> false, 
              "MessagesCurrentCount" -> BigInt(1), 
              "MessagesPendingCount" -> BigInt(1), 
              "State" -> "advertised_in_cluster_jndi", 
              "BytesCurrentCount" -> BigInt(100), 
              "Type" -> "JMSDestinationRuntime"))
  
    def assertError(error: String) {
      println(error)
      fail(error)
    }
    
    def assertJMSDestinationRuntime(destinationRuntime: JMSDestinationRuntime) {
      assert(destinationRuntime.name == "jms-module!yellowfire.jms.queue.in.accounting")
      assert(destinationRuntime.destinationType == "Queue")
      assert(destinationRuntime.paused == false)
 
      assert(destinationRuntime.insertion.paused == false)
      assert(destinationRuntime.insertion.state == "Insertion-Enabled")
      assert(destinationRuntime.consumption.paused == false);
	  assert(destinationRuntime.consumption.state == "Consumption-Enabled")
	  assert(destinationRuntime.production.paused == false);
	  assert(destinationRuntime.production.state == "Production-Enabled")
 
	  val messages = destinationRuntime.messages;
	  assert(messages.high == 7)
	  assert(messages.thresholdTime == 0)
	  assert(messages.deleted == 0)
	  assert(messages.pending == 1)
	  assert(messages.received == 74)
	  assert(messages.moved == 0)
	  assert(messages.current == 1)
	  
	  val bytes = destinationRuntime.bytes
	  assert(bytes.high == 5943)
	  assert(bytes.thresholdTime == 0)
	  assert(bytes.pending == 100)
	  assert(bytes.received == 51655)
	  assert(bytes.current == 100)
	  
	  val consumers = destinationRuntime.consumers
	  assert(consumers.current == 1)
	  assert(consumers.high == 2)
	  assert(consumers.total == 15)
    }
    
  	"A ReadResponse" should "converted to a JMSDestinationRuntime" in {
	  val result:Either[String, JMSDestinationRuntime] = ConvertToJMSDestinationRuntime(readResponse)
      
	  result.fold (assertError _, assertJMSDestinationRuntime _)  
  }
    
  "A client" should "be able to retrieve the JMS destinations from Weblogic" in {
	  import jolokia.weblogic._
	  import dispatch._
	  import Defaults._
	  
	  val http = new Http
	  val client = new WeblogicClient("localhost", 7001)
	  
	  val timestamp = new java.util.Date
	  val response = client.searchDestinations
	  
	  assert(response.status == 200, "status should be 200")
	  
	  assert(response.timestamp.getDay == timestamp.getDay)
	  assert(response.timestamp.getMonth == timestamp.getMonth)
	  assert(response.timestamp.getYear == timestamp.getYear)
	  assert(response.timestamp.getHours == timestamp.getHours)
	  assert(response.timestamp.getMinutes == timestamp.getMinutes)
  }
}
