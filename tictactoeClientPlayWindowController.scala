import akka.pattern.ask
import akka.util.Timeout
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.Await
import scalafx.application.Platform
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle , Circle}
import scalafx.scene.control.{Button , TextField , Label}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.GridPane
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.FXMLLoader
import scalafxml.core.NoDependencyResolver
import scalafx.beans.property._

@sfxml
class tictactoeClientPlayWindowController(
    private val leaveButton: Button ,
    private val player1Name: Label ,
    private val player2Name: Label ,
    private val button1: Button ,
    private val button2: Button ,
    private val button3: Button ,
    private val button4: Button ,
    private val button5: Button ,
    private val button6: Button ,
    private val button7: Button ,
    private val button8: Button ,
    private val button9: Button ,
    private val boardGrid: GridPane) {
  import ParticipantActor._
  
  var moveCount = 0
  val clickable: BooleanProperty = BooleanProperty(false)
  var win: BooleanProperty = BooleanProperty(false)
  var stage: Option[Stage] = None
  var board2 = Array(
      button1 ,
      button2 ,
      button3 ,
      button4 ,
      button5 ,
      button6 , 
      button7 ,
      button8 ,
      button9)
  
      for (i <- 0 to 8) {
        println("data:"+tictactoeRunUI.data(i))
        board2(i).setText(tictactoeRunUI.data(i))
      
      }
  
  player1Name.text <== tictactoeRunUI.player1Name
  player2Name.text <== tictactoeRunUI.player2Name
  
  // ---------------------------- METHODS ---------------------------- 
  def handleLeave(event: ActionEvent) {
    println("leave game")
    reset()
    Platform.runLater({
      tictactoeRunUI.showOriWindow
      
    })
  }
  
  def reset() {
    for(button <- board2) {
      button.setText("")
      moveCount = 0
   
    }
  }

  def showWin(name: String) {
   var alertWin = new Alert(Alert.AlertType.Information) {
       initOwner(stage getOrElse(tictactoeRunUI.stage))
       title = "Winner"
       headerText =  "Congratulations"
       contentText = s"$name have won"
        
     }.showAndWait() 
  }
  
  def showTie() {
    var alertWin = new Alert(Alert.AlertType.Information) {
      initOwner(stage getOrElse(tictactoeRunUI.stage))
      title = "No Winner"
      headerText =  "No Winner"
      contentText = "It's a Tie"
        
    }.showAndWait() 
  }
  
  def showStart() {
    val mayStart = new Alert(Alert.AlertType.Information) {
      initOwner(stage getOrElse(tictactoeRunUI.stage))
      title = "Start"
      contentText = "You may start"
      
    }.showAndWait()
    
    clickable.value = true
  }
  
  def showStop() {
    val mayStart = new Alert(Alert.AlertType.Information) {
      initOwner(stage getOrElse(tictactoeRunUI.stage))
      title = "Stop"
      contentText = "Wait for your turn"
      
    }.showAndWait()
    
    clickable.value = false
  }
  
  def clickOnButton(button: Int) { // Other player
    if (tictactoeRunUI.player ==  "p1") {
      board2(button).setText("O") 
      
    }
    
    else if (tictactoeRunUI.player == "p2") {
      board2(button).setText("X")
      
    }
  }
  
  def selfClickOnButton(button: Int) { // self click
    if (tictactoeRunUI.player ==  "p1") {
      //println("aaa")
      board2(button).setText("X")
     
    }
    
    else if (tictactoeRunUI.player == "p2") {
      //println("bbb")
      board2(button).setText("O")
      
    }
  }
  
  def checkValid(button: Int): Boolean = {
    if (board2(button).text.value == "X" || board2(button).text.value ==  "O") {
      val invalidMove = new Alert(Alert.AlertType.Information) {
        initOwner(stage getOrElse(tictactoeRunUI.stage))
        title = "Invalid move"
        contentText = "Do a valid move"
      
      }.showAndWait()
      
      return false
    }
    
    else {
      return true
      
    }
  }
  
  def winCondX: Boolean = {
   if (
       ((button1.text.value == "X") && (button2.text.value == "X") && (button3.text.value == "X")) ||
       ((button4.text.value == "X") && (button5.text.value == "X") && (button6.text.value == "X")) ||
       ((button7.text.value == "X") && (button8.text.value == "X") && (button9.text.value == "X")) ||
       ((button1.text.value == "X") && (button4.text.value == "X") && (button7.text.value == "X")) ||
       ((button2.text.value == "X") && (button5.text.value == "X") && (button8.text.value == "X")) ||
       ((button3.text.value == "X") && (button6.text.value == "X") && (button9.text.value == "X")) ||
       ((button1.text.value == "X") && (button5.text.value == "X") && (button9.text.value == "X")) ||
       ((button7.text.value == "X") && (button5.text.value == "X") && (button3.text.value == "X"))) {
     return true
     
   }
   
   else {
     return false
     
   }
  }
  
  def winCondO: Boolean = {
    if (
        ((button1.text.value == "O") && (button2.text.value == "O") && (button3.text.value == "O")) ||
        ((button4.text.value == "O") && (button5.text.value == "O") && (button6.text.value == "O")) ||
        ((button7.text.value == "O") && (button8.text.value == "O") && (button9.text.value == "O")) ||
        ((button1.text.value == "O") && (button4.text.value == "O") && (button7.text.value == "O")) ||
        ((button2.text.value == "O") && (button5.text.value == "O") && (button8.text.value == "O")) ||
        ((button3.text.value == "O") && (button6.text.value == "O") && (button9.text.value == "O")) ||
        ((button1.text.value == "O") && (button5.text.value == "O") && (button9.text.value == "O")) ||
        ((button7.text.value == "O") && (button5.text.value == "O") && (button3.text.value == "O"))) {
      return true 
      
    }
    
    else {
      return false
      
    }
  }
  
  def checkWin {
    if (winCondX) {
      showWin(tictactoeRunUI.player)
      tictactoeRunUI.clientActor ! GameEnd
    }
    
    else if (winCondO) {
      showWin(tictactoeRunUI.player)
      tictactoeRunUI.clientActor ! GameEnd
    }
    
    else if (!(winCondX && winCondO) && (moveCount == 9)) {
      showTie()
      tictactoeRunUI.clientActor ! GameEnd
      
    }
  }
  // --------------------------------------------------------------------
  
  // ---------------------------- GAME START ----------------------------
  for ((button: Button , count) <- board2.zipWithIndex) {
    button.disable <== !clickable
    button.onAction = handle {
      //println("Clickable:"+ button.disable.value)
      val myCount: Int = count
      
      if (clickable.value == true) {
        //println("click" + tictactoeRunUI.player)
        if (tictactoeRunUI.player == "p1" && checkValid(myCount)) {
          implicit val timeout: Timeout = Timeout(5 seconds)
          val a = tictactoeRunUI.clientActor ? ClickCount(myCount)
          Await.result(a , 5 seconds)
          moveCount += 1
          checkWin
          
        }
        
        else if (tictactoeRunUI.player == "p2" && checkValid(myCount)) {
          button.onAction = handle {
            implicit val timeout: Timeout = Timeout(5 seconds)
            val a = tictactoeRunUI.clientActor ? ClickCount(myCount)
            Await.result(a , 5 seconds)
            moveCount += 1
            checkWin
            
          }
        }
      }
    }
  }
  // ---------------------------- GAME END ----------------------------
}
