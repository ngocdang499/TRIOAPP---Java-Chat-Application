package Server;

import Communicate.FileServer;
import Communicate.SendMsg;
import Protocol.Tags;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Class.User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class AcceptFileBox {
    public static void display(String tittle, String[] id_fname, Socket socket, User current, ArrayList<User> peerLst){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(tittle);
        window.setWidth(300);
        window.setHeight(200);

        Label label = new Label("Do you want to accept file sent from " + id_fname[0] + "?");
        label.setWrapText(true);
        Button btnYes = new Button("YES");
        btnYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(window);

                if(selectedDirectory != null){
                    FileServer fs = new FileServer(60000,selectedDirectory.getAbsolutePath()
                            + "/" + id_fname[1]);
                    fs.start();

                    ObjectOutputStream sender = null;
                    try {
                        sender = new ObjectOutputStream(socket.getOutputStream());
                        sender.writeObject(Tags.FILE_ACCEPT_TAG); sender.flush();
                        window.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }else{
                    AlertBox a = new AlertBox();
                    a.display("Error", "Error occured!");
                    try {
                        ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
                        sender.writeObject(Tags.FILE_ACCEPT_TAG); sender.flush();
                        window.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    window.close();
                }
            }
        });
        Button btnNo = new Button("NO");
        btnNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                String host = SendMsg.getIP(id_fname[0],peerLst);
                ObjectOutputStream sender = null;
                try {
                    sender = new ObjectOutputStream(socket.getOutputStream());
                    sender.writeObject(Tags.FILE_REFUSE_TAG); sender.flush();
                    window.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                window.close();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,btnYes,btnNo);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}