package Server;

import GUI_Client.ChatUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class SettingBox {
    private TextField grp_name;
    private TextField opacity;

    public SettingBox() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Setting");
        window.setWidth(400);
        window.setHeight(300);

        Label label1 = new Label("Choose background image:");
        label1.setWrapText(true);
        grp_name = new TextField();
        grp_name.setPromptText("Path to image");
        grp_name.setMaxWidth(200);
        Button btnChoose = new Button("Choose File");
        btnChoose.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(new Stage());
                String path = selectedFile.getAbsolutePath();
                System.out.println(path);
                grp_name.setText(path);
                //res = true;
            }
        });
        opacity = new TextField();
        opacity.setPromptText("0.5");
        opacity.setMaxWidth(100);
        Button btnOK = new Button("OK");
        btnOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (!grp_name.getText().isBlank() && !opacity.getText().isBlank()) {

                     window.close();
                }
            }
        });
        Button btnDefault = new Button("Default");
        btnDefault.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                grp_name.setText("");
                window.close();
                //res = true;
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1,grp_name,btnChoose,opacity,btnOK,btnDefault);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
    public String returnPath() {
        return grp_name.getText();
    }
    public String returnOpacity() {
        return opacity.getText();
    }
}