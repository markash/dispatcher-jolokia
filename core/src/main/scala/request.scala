package jolokia.request

import dispatch._
import org.json4s._

case class ObjectName(domain: String, properties: Map[String, String], value: String)

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
		
		ObjectName(name, properties, value)
	}
}

trait Method extends (Req => Req) {
	def complete: Req => Req
	def apply(req: Req): Req = complete(req)
}

case class Search(query: String) extends Method {
	def complete = _ / "search" / query
}

case class Read(objectName: ObjectName) extends Method {
	def complete = _ / "read" / objectName.value.replace("!", "!!")
}

case class Test(objectName: String) extends Method {
	def complete = _ / "read" / objectName.replace("!", "!!")
}

class AbstractClient(hostName: String, port: Int) extends (Method => Req) {
	def host = :/ (hostName, port) / "jolokia"
	def apply (method: Method): Req = method(host) 
}

import java.util.Calendar
import java.text.SimpleDateFormat

trait Show[A] {
	def shows(a : A) : String
}

object Show {
	def showA[A]: Show[A] = new Show[A] {
		def shows(a: A) = a.toString()
	}

	implicit val stringShow 	= showA[String]
	implicit val intShow 		= showA[Int]
	implicit val bigIntShow 	= showA[BigInt]
	implicit val booleanShow 	= showA[Boolean]
	implicit val longShow 		= showA[Long]
	
	private val yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd")
	implicit val calendarShow = new Show[Calendar] {
		def shows(a: Calendar) = yyyyMMdd.format(a.getTime)
	}
}


