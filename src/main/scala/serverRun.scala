import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global

object serverRun extends App {
  // Actor System
  val a = ActorSystem("tictactoeServer")
  val serverActor = a.actorOf(Props[ServerActor] , "ServerActor")
  
  import ServerActor._
  
  serverActor ! StartServer
  
}