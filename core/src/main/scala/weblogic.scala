package jolokia.weblogic

import jolokia.response._

case class Messages(current: BigInt, pending: BigInt, high: BigInt, deleted: BigInt, moved: BigInt, received: BigInt, thresholdTime: BigInt) 
case class Bytes(current: BigInt, high: BigInt, pending: BigInt, received: BigInt, thresholdTime: BigInt)
case class Consumers(current: BigInt, high: BigInt, total: BigInt)
case class Consumption(paused: Boolean, state: String)
case class Insertion(paused: Boolean, state: String)
case class Production(paused: Boolean, state: String)

case class JMSDestinationRuntime(
    name: String,
    parent: Any,
    paused: Boolean,
    state: String,
    destinationType: String,
    destinationInfo: Any,
    messages:  Messages, 
    bytes: Bytes, 
    consumers: Consumers, 
    consumption: Consumption, 
    insertion: Insertion, 
    production: Production)
    
object ConvertToMessages extends (ReadResponse => Messages) {
  def apply(response: ReadResponse): Messages = {
		  new Messages(
			  response.value("MessagesCurrentCount").asInstanceOf[BigInt],
			  response.value("MessagesPendingCount").asInstanceOf[BigInt],
			  response.value("MessagesHighCount").asInstanceOf[BigInt],
			  response.value("MessagesDeletedCurrentCount").asInstanceOf[BigInt],
			  response.value("MessagesMovedCurrentCount").asInstanceOf[BigInt],
			  response.value("MessagesReceivedCount").asInstanceOf[BigInt],
			  response.value("MessagesThresholdTime").asInstanceOf[BigInt]
		  )
  }
}

object ConvertToBytes extends (ReadResponse => Bytes) {
  def apply(response: ReadResponse): Bytes = {
		  new Bytes(
			  response.value("BytesCurrentCount").asInstanceOf[BigInt],
			  response.value("BytesHighCount").asInstanceOf[BigInt],
			  response.value("BytesPendingCount").asInstanceOf[BigInt],
			  response.value("BytesReceivedCount").asInstanceOf[BigInt],
			  response.value("BytesThresholdTime").asInstanceOf[BigInt]
		  )
  }
}

object ConvertToConsumers extends (ReadResponse => Consumers) {
  def apply(response: ReadResponse): Consumers = {
    new Consumers(
        response.value("ConsumersCurrentCount").asInstanceOf[BigInt],
		response.value("ConsumersHighCount").asInstanceOf[BigInt],
		response.value("ConsumersTotalCount").asInstanceOf[BigInt]
    )
  }
}

object ConvertToConsumption extends (ReadResponse => Consumption) {
  def apply(response: ReadResponse): Consumption = {
    new Consumption(
        response.value("ConsumptionPaused").asInstanceOf[Boolean],
        response.value("ConsumptionPausedState").asInstanceOf[String]
    )
  }
}

object ConvertToInsertion extends (ReadResponse => Insertion) {
  def apply(response: ReadResponse): Insertion = {
    new Insertion(
        response.value("InsertionPaused").asInstanceOf[Boolean],
        response.value("InsertionPausedState").asInstanceOf[String]
    )
  }
}

object ConvertToProduction extends (ReadResponse => Production) {
  def apply(response: ReadResponse): Production = {
    new Production(
        response.value("ProductionPaused").asInstanceOf[Boolean],
        response.value("ProductionPausedState").asInstanceOf[String]
    )
  }
}

object ConvertToJMSDestinationRuntime extends (ReadResponse => Either[String, JMSDestinationRuntime]) {
	def apply(response: ReadResponse): Either[String, JMSDestinationRuntime] = response.status match {
	  
	  case 200 => Right(new JMSDestinationRuntime(
					    response.value("Name").asInstanceOf[String],
					    response.value("Parent"),
					    response.value("Paused").asInstanceOf[Boolean],
					    response.value("State").asInstanceOf[String],
					    response.value("DestinationType").asInstanceOf[String],
					    response.value("DestinationInfo"),
					    ConvertToMessages(response),
					    ConvertToBytes(response),
					    ConvertToConsumers(response),
					    ConvertToConsumption(response),
					    ConvertToInsertion(response),
					    ConvertToProduction(response)
					))
	  case _ => Left("No correct type")
	}
	
	implicit def readResponseToJMSDestinationRuntime(response: ReadResponse): Either[String, JMSDestinationRuntime] = ConvertToJMSDestinationRuntime(response)
}

class WeblogicClient(host: String, port: Int) extends Client(host, port) {
    def searchDestinations(): SearchResponse = search("com.bea:*,Type=JMSDestinationRuntime")
}

object Application {
	
	def test() = {		
		val client = new WeblogicClient("localhost", 7001)
		val searchResponse = client.searchDestinations

		val objectName = searchResponse.value(0)
		val readResponse = client.read(objectName)

		println(readResponse)
		
		val messageStats = ConvertToJMSDestinationRuntime(readResponse)

		messageStats
	}
	def json(args: Array[String]) {
		val client = new WeblogicClient("localhost", 7001)
		val searchResponse = client.searchDestinations

		val objectName = searchResponse.value(0)
		val readResponse = client.read(objectName)

		val statsJson = readResponse.value

		println(statsJson.values)
	}
	
	def main(args: Array[String]) {
		println(test)
	}
}
