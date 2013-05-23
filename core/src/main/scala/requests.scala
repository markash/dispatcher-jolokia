package jolokia.request

import dispatch._
import org.json4s._

trait Method extends (Req => Req) {
	def complete: Req => Req
	def apply(req: Req): Req = complete(req)
}

case class Search(query: String) extends Method {
	def complete = _ / "search" / query
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


