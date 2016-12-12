import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scalafx.application.Platform
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.control.{Button , Alert , TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader , NoDependencyResolver}

@sfxml
class tictactoeClientFrontWindowController(
    private val quitButton: Button , 
    private val connectButton: Button ,
    private val nameField: TextField , 
    private val serverPath: TextField) {
  import ParticipantActor._

  var actor: Option[ActorRef] = None
  var stage: Option[Stage] = None
  //val ip = "127.0.0.1"
  //val port = 5176
  
  serverPath.text = "akka.tcp://tictactoeServer@127.0.0.1:5176/user/ServerActor"

  def serverLocation: String = {
    return serverPath.text.value
    
  }
  
  def handleQuit(event: ActionEvent) {
    System.exit(0)
    
  }
  
  def handleConnect(event: ActionEvent) {
    //val clientAR =  tictactoeRunUI.clientActor
    implicit val timeout: Timeout = Timeout(5 seconds)
    // get nameField , client actor ref and send
    tictactoeRunUI.clientActor ? Join(nameField.text.value , tictactoeRunUI.clientActor) foreach {
      case JoinSuccess(name , ref) => 
        //println("Connected successfully")
        //println("player1")
        tictactoeRunUI.player1Name.value = name
        println("p1 name:"+tictactoeRunUI.player1Name)
        tictactoeRunUI.clientActor ! JoinSuccess
        Platform.runLater({
          tictactoeRunUI.showPlayWindow()
          
        })
        
      case AlreadyJoin => 
        Platform.runLater({
          val cfAlert = new Alert(Alert.AlertType.Information) {
            initOwner(stage getOrElse(tictactoeRunUI.stage))
            title = "Error"
            headerText = "Already Joined"
            
          }.showAndWait()
        })
        
      case p2(name , playerOne) =>
        tictactoeRunUI.clientActor ! player2Join
        tictactoeRunUI.clientActor ! p2(name , playerOne)
        tictactoeRunUI.player2Name.value = name
        println("p2 name:"+ tictactoeRunUI.player2Name)
       // println("player2")
        Platform.runLater({
          tictactoeRunUI.showPlayWindow()
              
        })
    }
  }
  
  def showJoinedError() {
    val connectedAlert = new Alert(Alert.AlertType.Information) {
      initOwner(stage getOrElse(tictactoeRunUI.stage))
      title = "Already Joined Error"
      contentText = "Already joined"
      
    }.showAndWait()
  }
  
  def showConnectionError() {
    val connectionAlert = new Alert(Alert.AlertType.Information) {
      initOwner(stage getOrElse(tictactoeRunUI.stage))
      title = "Connection Error"
      contentText = "Failed to connect"
      
    }.showAndWait()
  }
}