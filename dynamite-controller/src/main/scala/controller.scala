package controller

import akka.actor.ActorDSL._
import akka.actor.ActorSystem

import java.net.ServerSocket
import java.io.PrintStream
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import collection.mutable

object controller {
	def main(args: Array[String]): Unit = {
	  
	  case class Client(sock: Socket, is: BufferedReader, ps: PrintStream, name: String)
	  
	  implicit val system = ActorSystem("clientacceptor")

	  val clients = new mutable.ArrayBuffer[Client] with mutable.SynchronizedBuffer[Client] {}
	  val ss = new ServerSocket(4343)

	  val clientAcceptor = actor(system)(new Act {
	  	become {
	  		case true => {
	  			while(true) {
	  				val sock = ss.accept()
	  				val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
	  				val ps = new PrintStream(sock.getOutputStream())

	  				val clientAdder = actor(system)(new Act {
	  					become {
	  						case true => clients += Client(sock, is, ps, (clients.length + 1).toString)
	  					}
	  				})

	  				clientAdder ! true
	  			}
	  		}
	  	}
	  })

	  clientAcceptor ! true
	  
	  // This is what happens whenever a client sends something to the coordinator.
	  while(true) {
	    for(client <- clients) {
	      if(client.is.ready) {
	        val request = client.is.readLine
	        client.ps.println(switchboard(request))
	      }
	    }
	  }
	}
	
	// Accepts client request as a String, splits request into tokens, calls appropriate
	// function on database, returns result of request.
	def switchboard(request:String): String = {	  
	  val tokens = request.split(" ")
	  val command = tokens(0)

	  def callController(command:String): String = command match {
	  	case "addServer" 	=> hashRing.addServerToRing(tokens(1)).toString
	  	case "get"			=> hashRing.getValue(tokens(1)).toString
	  	case "listServers"	=> hashRing.listServers().toString
	  	case "set"			=> hashRing.addPairToRing(tokens(1), tokens(2)).toString
	  	case _				=> "false"
	  }

	  callController(command)
	}
}






















