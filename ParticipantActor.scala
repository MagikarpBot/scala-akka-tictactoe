import akka.actor._
import akka.remote._
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scalafx.application.Platform

class ParticipantActor(
    val control: tictactoeClientFrontWindowController#Controller , 
    val controller: tictactoeClientPlayWindowController#Controller) extends Actor {
  import ParticipantActor._
  import ServerActor._

  var player1: Option[ActorRef] = None
  var player2: Option[ActorRef] = None

  def host = context.actorSelection(control.serverLocation)
  
  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[akka.remote.DisassociatedEvent])
    context.system.eventStream.subscribe(self, classOf[akka.remote.AssociationErrorEvent])

  }

  def receive = {
    case Join(name , ref) =>
      implicit val timeout: Timeout = Timeout(5 seconds)
      //println(host)
      host ? Connect(name , ref) pipeTo sender
      //var name1 = name
      println(s"name: $name")
      
    case JoinSuccess =>
      tictactoeRunUI.player = "p1"
      //tictactoeRunUI.player1Name = name1
      context.become(join)
 
    case AlreadyJoin =>
      Platform.runLater({
        control.showJoinedError()
      
      })
      
    case AssociationErrorEvent(_ , localAddress , remoteAddress , _ , _) => 
      Platform.runLater({
        control.showConnectionError()
        
      })
          
    case DisassociatedEvent(localAddress , remoteAddress , _)  =>
      Platform.runLater({
        control.showConnectionError()
        
      })
      
    case Click =>
      context.become(click)
      
    case Done =>
      context.unbecome()
      
    case player2Join =>
      //println("set player 2")
      tictactoeRunUI.player = "p2"
      context.become(p2)

 
  }
   // player1 become join
  def join: Receive = {
    case Join(name , ref) =>
      sender ! AlreadyJoin
       
    case p1(name , secondPlayer) =>
      // knows 2nd player
      // send start to p1 and 2nd player
      player2 = Some(secondPlayer)
      tictactoeRunUI.player2Name.value = name

       
    case AssociationErrorEvent(_ , localAddress , remoteAddress , _ , _) =>
      Platform.runLater({
       control.showConnectionError()
        
      })
          
    case DisassociatedEvent(localAddress , remoteAddress , _) =>
      Platform.runLater({
       control.showConnectionError()
        
      })
    case Start => 
      Platform.runLater({
        controller.showStart()
        controller.clickable.value = true
        println("click true")
      })
      context.become(click)
      
  }
  
   // player2 become p2
   def p2: Receive = {
     case Join(name , ref) =>
       sender ! AlreadyJoin
       
     case AssociationErrorEvent(_ , localAddress , remoteAddress , _ , _) => 
       Platform.runLater({
         control.showConnectionError()
        
       })
          
     case DisassociatedEvent(localAddress , remoteAddress , _)  =>
       Platform.runLater({
         control.showConnectionError()
        
       })
       
     case Start => // show stop
       Platform.runLater({
         controller.showStop()
         
       })
       //println("receivedstart")
       context.become(unclick)
       
     case p2(name , player1) =>
       //println("setting playaer")
       this.player1 = Some(player1)
       tictactoeRunUI.player1Name.value = name
       player1 ! Start
       //println("sending start to "+player1)
       self ! Start
       
   }
   
   def click: Receive = {
     case AssociationErrorEvent(_ , localAddress , remoteAddress , _ , _) => 
       Platform.runLater({
         control.showConnectionError()
        
       })
          
     case DisassociatedEvent(localAddress , remoteAddress , _)  =>
       Platform.runLater({
         control.showConnectionError()
        
       })
     
     case ClickCount(myCount) =>
       println("clickcount")
       Platform.runLater({
         controller.clickable.value = false
         controller.selfClickOnButton(myCount)
       })
      
       if (tictactoeRunUI.player == "p1") {
         player2.get ! SelectCount(myCount)
         //println("SelectCount(myCount):"+SelectCount(myCount))
         context.become(unclick)
         
       }
       
       else if (tictactoeRunUI.player == "p2") {
         player1.get ! SelectCount(myCount)
         context.become(unclick)
         
       }
       
       sender ! Done
       
     case Reset =>
       reset()
     
     case GameEnd =>
       controller.clickable.value = false
       
   }
   
   def unclick: Receive = {
     case AssociationErrorEvent(_ , localAddress , remoteAddress , _ , _) =>
       Platform.runLater({
         control.showConnectionError()
        
       })
          
     case DisassociatedEvent(localAddress , remoteAddress , _)  =>
       Platform.runLater({
         control.showConnectionError()
        
       })
       
     case ClickCount(myCount) =>
       Platform.runLater({
         controller.showStop()
         //controller.clickOnButtonX(myCount)
       
       })
       sender ! Done
       
     case SelectCount(myCount) =>
       context.become(click)
       //println("selectCount execute")
       Platform.runLater({
         controller.clickable.value = true
         controller.clickOnButton(myCount)
       
       })
       
     case Reset  =>
       reset()
    
     case GameEnd =>
       controller.clickable.value = false
       
   }
   
   def reset() {
     player1 = None
     player2 = None
     
     context.unbecome()
     
   }
}

object ParticipantActor {
  // external message
  case class Winner(name: String)
  case class Join(name: String , ref: ActorRef)
  case class JoinSuccess(name: String , ref: ActorRef)
  case class p2(name: String , playerOwn: ActorRef)
  case class p1(name: String , playerTwo: ActorRef)
  case class SelectCount(myCount: Int)
  case class ClickCount(myCount: Int)
  
  // internal message
  case object ConnectSuccess
  case object ConnectFail
  case object JoinFail
  case object Click
  case object AlreadyJoin
  case object player2Join
  case object Done
  case object Start
  case object Reset
  case object GameEnd
  
}