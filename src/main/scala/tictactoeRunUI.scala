import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.application.JFXApp
import scalafx.application.Platform
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.Includes._
import scalafx.scene.Scene
import scalafxml.core.FXMLLoader
import scalafxml.core.NoDependencyResolver

object tictactoeRunUI extends JFXApp {
  // load fxml
  lazy val data: Array[String] = Array("" , "" , "" , "" , "" , "" , "" , "" , "")
  val loader1 = new FXMLLoader(null , NoDependencyResolver)
  val loader2 = new FXMLLoader(null , NoDependencyResolver)
  var player1Name: StringProperty = new StringProperty("")
  var player2Name: StringProperty = new StringProperty("")
  
  // load resource(fxml)
  val resource = getClass.getResourceAsStream("tictactoeFrontWindow.fxml")
  val resource2 = getClass.getResourceAsStream("tictactoePlayWindow.fxml")
  loader1.load(resource)
  loader2.load(resource2)
  
  // get root pane and controller
  val rootPane = loader1.getRoot[javafx.scene.layout.BorderPane]
  val rootPane2  = loader2.getRoot[javafx.scene.layout.BorderPane]
  val frontWindowController = loader1.getController[tictactoeClientFrontWindowController#Controller]
  val playWindowController = loader2.getController[tictactoeClientPlayWindowController#Controller]
  // create Actor system
  val system = ActorSystem("TicTacToe")
  val clientActor = system.actorOf(Props(new ParticipantActor(frontWindowController , playWindowController)))
  var player = ""
  
  val myScene = new Scene(width = 400 , height = 400) {
      root = rootPane
    
  }
  
  val playScene = new Scene(width = 700 , height = 500) {
      root = rootPane2     
    
  }
  
  stage = new PrimaryStage() {
    title = "Tic Tac Toe Game Client"
    scene = myScene
    
    onCloseRequest = handle {
      system.terminate() foreach {
        case _ => Platform.exit()     
        
      }
    }
  }
  
  def showPlayWindow() {
    stage.scene = playScene
    
  }
  
  def showOriWindow() {
    stage.scene = myScene
    reset()
    
  }
  
  def reset() {
    player = ""
    
  }
 
  frontWindowController.stage = Some(stage)
  frontWindowController.actor = Some(clientActor)
  
}
