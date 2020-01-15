import GUI_Client.ChatUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Display UI
        Parent root = FXMLLoader.load(getClass().getResource("GUI_Client/LoginUI.fxml"));
        primaryStage.setTitle("TrioApp 1.0");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                ;
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
