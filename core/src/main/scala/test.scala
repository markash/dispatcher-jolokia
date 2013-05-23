package jolokia.test

import dispatch._
import Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._
import jolokia.request._
import java.util.Date

//(

case class ObjectName(domain: String, properties: Map[String, String])

object ObjectName {
	def parse(value: String): ObjectName = {
		/* Split the JMX name into parts [domain][prop-key][prop-val][prop-key][prop-val]...[n-key][n-val] */
		val items: Array[String] = value.split(Array(':', ',', '='))
		/* First item is domain */
		val name = items(0)
		val p: Array[String] = items.drop(1)
		/* Pair up items into a tuple2 so that toMap can convert it to a Map[String, String] */
		val iter = for {group <- p.sliding(2, 2)  } yield (group(0), group(1))
		val properties: Map[String, String] = iter toMap
		
		ObjectName(name, properties)
	}
}

case class Request(mbean: String, requestType: String)

case class SearchResponse(timestamp: Date, status: Int, request: Request, value: List[ObjectName]) {
	def this(timestamp: BigInt, status: Int, request: Request, value: List[String]) = 
		this(
			new Date((1000L * timestamp).toLong), 
			status, 
			request, 
			value.map(name => ObjectName.parse(name)))
}


object Test {
	implicit val formats = DefaultFormats

	def main(args: List[String]): SearchResponse = {
		val http = new Http
		val client = new AbstractClient("localhost", 7001)
		val f = http(client(new Search("com.bea:*,Type=JMSDestinationRuntime")) OK as.json4s.Json)
		val json = f() transformField { case ("type", x) => ("requestType", x) }
		
		json.extract[SearchResponse]
	}
}