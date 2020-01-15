package Server;

import Communicate.*;
import GUI_Client.ChatUI;
import Protocol.Tags;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GroupChatBox {
    public static void display(String tittle){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(tittle);
        window.setWidth(400);
        window.setHeight(250);

        Label label1 = new Label("Input Group's name:");
        label1.setWrapText(true);
        TextField grp_name = new TextField();
        grp_name.setPromptText("Group name");
        grp_name.setMaxWidth(250);
        Label label2 = new Label("Input member's name: \n(Each name is separated by \na comma and no extra space is used");
        label2.setWrapText(true);
        TextField mem_name = new TextField();
        mem_name.setPromptText("Member name1,Member name2,Member name3");
        mem_name.setMaxWidth(250);
        boolean res;
        Button btnOK = new Button("Create Group");
        btnOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                ChatUI.gmem_list = mem_name.getText();
                ChatUI.grp_name = grp_name.getText();
                window.close();
                //res = true;
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1,grp_name,label2,mem_name,btnOK);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}