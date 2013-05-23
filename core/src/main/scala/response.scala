package jolokia.response

import dispatch._
import org.json4s._

object Search extends Parse {
	val statuses = 'statuses.![List[JValue]]
	val search_metadata = 'search_metadata.![JObject]
}


trait ReadJs[A] {
	import ReadJs.=>?
	val readJs: JValue =>? A
}

object ReadJs {
	type =>?[-A, +B] = PartialFunction[A, B]
	def readJs[A](pf: JValue =>? A): ReadJs[A] = new ReadJs[A] {
		val readJs = pf
	}

	implicit val listRead:   ReadJs[List[JValue]] =  	readJs { case JArray(v) => v }
	implicit val objectRead: ReadJs[JObject] = 			readJs { case JObject(v) => JObject(v) }
	implicit val bigIntRead: ReadJs[BigInt] =  			readJs { case JInt(v) => v }
	implicit val intRead:    ReadJs[Int] =     			readJs { case JInt(v) => v.toInt }
	implicit val stringRead: ReadJs[String] = 			readJs { case JString(v) => v }
	implicit val boolRead:   ReadJs[Boolean] = 			readJs { case JBool(v) => v }
}

trait Parse {
	def parse[A: ReadJs](js: JValue): Option[A] = implicitly[ReadJs[A]].readJs.lift(js)
	def parse_![A: ReadJs](js: JValue): A = parse(js).get
	def parseField[A: ReadJs](key: String)(js: JValue): Option[A] = parse[A](js \ key)
	def parseField_![A: ReadJs](key: String)(js: JValue): A = parseField(key)(js).get
	implicit class SymOp(sym: Symbol) {
		def apply[A: ReadJs]: JValue => Option[A] = parseField[A](sym.name)_
		def ![A: ReadJs]: JValue => A = parseField_![A](sym.name)_
	}
}

object Result extends Parse {
	val timestamp = 'timestamp[BigInt]
	val status = 'status[Int]
	val request = 'request[JObject]
	val value = 'value[List[JValue]]
}

