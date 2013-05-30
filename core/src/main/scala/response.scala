package jolokia.response

import dispatch._
import Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._
import jolokia.request._
import java.util.Date

case class Request(mbean: String, requestType: String)

case class SearchResponse(timestamp: Date, status: Int, request: Request, value: List[ObjectName]) {
	def this(timestamp: BigInt, status: Int, request: Request, value: List[String]) = 
		this(
			new Date((1000L * timestamp).toLong), 
			status, 
			request, 
			value.map(name => ObjectName.parse(name)))
}

case class ReadResponse(timestamp: Date, status: Int, request: Request, value: Map[String, Any]) {
	def this(timestamp: Date, status: Int, request: Request, value: JObject) = 
		this(
			timestamp, 
			status, 
			request, 
			value.values)	
}


class Client(host: String, port: Int) {
	implicit val formats = DefaultFormats

	val http = new Http
	val client = new AbstractClient(host, port)

	def error(errorCode: Int): JObject = {
			
	   val request = JObject(List( 
	       ("mbean", JString("")), 
	       ("type", JString("search")) 
	   ))
	      
       JObject(List(
           ("timestamp", JInt(new java.util.Date().getTime() / 1000)),
           ("status", JInt(errorCode)),
           ("request", request)
       ))
	}
	
	def search(query: String): SearchResponse = {
		val f = http(client(new Search(query)) OK as.json4s.Json).option
		val json = f().getOrElse(error(404)) transformField { case ("type", x) => ("requestType", x) }
		json.extract[SearchResponse]
	}
	
	def read(objectName: ObjectName): ReadResponse = {
		val f = http(client(new Read(objectName)) OK as.json4s.Json)
		val json = f() transformField { case ("type", x) => ("requestType", x) }
		json.extract[ReadResponse]
	}
	
	/* Used to covert a json4s JInt to a java.util.Date in the ReadResponse */
	implicit def jsonIntToDate(timestamp: JInt): java.util.Date = new Date((1000L * timestamp.values).toLong)
}
