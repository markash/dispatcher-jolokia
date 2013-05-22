package dispatch.jolokia.request

import dispatch._
import org.json4s._

trait Method extends (Req => Req) {
	def complete: Req => Req
	def apply(req: Req): Req = complete(req)
}

case class Search(query: String) extends Method {
	def complete = _ / "search" / query
}

//case object Search {
//	def apply(query: String): Search = Search(query)
//}

class AbstractClient(hostName: String, port: Int) extends (Method => Req) {
	def host = :/ (hostName, port) / "jolokia"
	def apply (method: Method): Req = method(host) 
}

