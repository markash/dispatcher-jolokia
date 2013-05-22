Yellowfire Dispatcher Jolokia Plugin
====================================

How to use
----------
import dispatch._
import Defaults._
import dispatch.jolokia.request._

val client = new AbstractClient("localhost", 7001)
val result = http(client(Search("com.bea:*,Type=JMSDestinationRuntime")) OK as json4s.Json)
val json = result()