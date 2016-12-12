import akka.actor._

class ServerActor extends Actor {
  import ServerActor._
  import ParticipantActor._

  var playerOne: Option[ActorRef] = None
  var playerTwo: Option[ActorRef] = None
  
  def receive = {
    case Connect(name , ref) =>
      //println("connect to server")
      playerOne = Some(ref)
      context.become(waiting)
      sender ! JoinSuccess(name , ref)
      
    case StartServer => 
      println("Starting tictactoe server")
    
    case Kill =>
      context.system.shutdown()
      
  }

  def waiting: Receive = {
    case Connect(name , ref) =>
      if (ref !=  playerOne) {
        //println("waiting for p2")
        playerTwo = Some(ref)
        //start game
        //send the p1(secondref)
        playerOne.get ! p1(name , playerTwo.get)
        sender ! p2(name , playerOne.get)
        
      }
      
      else {
        //  send error msg
        sender ! AlreadyJoin
    
      }

      context.unbecome()
  }
}

object ServerActor {
  // external message
  case class Connect(name: String , ref: ActorRef)
  // internal message
  case object StartServer
  
}