package GUI_Client;
import Communicate.*;
import Protocol.AttachTagsMsg;
import Protocol.DetachTagsMsg;

import Protocol.Tags;
import Server.AcceptFileBox;
import Server.AlertBox;
import Server.GroupChatBox;
import Server.SettingBox;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import Class.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.*;

import static Communicate.CustomerService.encode;


public class ChatUI implements Initializable {

    private static Integer TXTPORT = 50000;
    private static Integer SVRPORT = 44000;
    private static Integer FILEPORT = 60000;
    //private static String current_peer = "";
    public static String SERVER = "";
    private static Socket socket;
    private boolean loop = false;


    @FXML
    Button btnLogout;

    @FXML
    VBox scrlChat;

    @FXML
    VBox vbFriend;

    @FXML
    TextArea txtArea;

    @FXML
    Button btnSearch;

    @FXML
    TextField txtSearch;

    @FXML
    Button btnMessage;

    @FXML
    Button btnSetting;

    @FXML
    ImageView imgBackground;

    private static User current = new User();
    private ServerSocket serverSocket = null;
    public static ArrayList<User> peerLst = new ArrayList<User>();
    public static ArrayList<User> customerRequest = new ArrayList<User>();

    //Variables for handling customer service
    private static ServerSocket serverWeb;
    private static Socket clientSocket;

    public void transferUserData(User user, String List, String server){
        current = new User(user.getUsrID(),user.getUsrIP(),user.getUsrPasswd(),user.getUsrPort(),user.getUsrStatus());

        System.out.println("transferData: "+user.getUsrID());

        SERVER = server;
        //Display the message
        current = new User(user.getUsrID(),user.getUsrIP(),user.getUsrPasswd(),user.getUsrPort(),user.getUsrStatus());

        peerLst = DetachTagsMsg.getSendIP(List);

        if(peerLst!=null) {
            System.out.println("List :" + peerLst.size());
            int i;
            for (i = 0; i < peerLst.size(); i++) {
                System.out.println(peerLst.get(i).getUsrIP());
                if (peerLst.get(i).getUsrID().matches(current.getUsrID()))
                    current.setUsrIP(peerLst.get(i).getUsrIP());
                else {
                    createPeerTab(peerLst.get(i));
                }
            }
            SendMsg.broadcastStatus(current, peerLst, 1);
        }
    }

    private static Socket socketClient;

    @FXML
    TabPane tabPane;

    private void genCommingMess(String u, String s) {
        String tab_name = u;
        String peer_name = u;
        String peer_lst = "";
        if(u.contains("~")) { // Group chat
            System.out.println("U :"+u);
            String[] tmp = u.split(":",3);
            //System.out.println(tmp. +" + " +);
            tab_name = tmp[0];
            peer_name = tmp[1];
            peer_lst = tmp[2];
        }

        if(u.matches("-.*-")) { // Customer request support
            String cName = u.substring(1,u.length()-1);
            User ctm = new User(cName,SERVER,"",SVRPORT,1);
            customerRequest.add(ctm);
        }

        for (Tab t : tabPane.getTabs()) {
            if (t.getText().matches(tab_name)) {
                AnchorPane pane = (AnchorPane) t.getContent();
                ScrollPane scrollChat = (ScrollPane) pane.getChildren().get(0);
                scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                VBox vb = (VBox) scrollChat.getContent();

                VBox tmpB = new VBox();
                Label usr = new Label(peer_name);
                Label mess = new Label(s);
                mess.setWrapText(true);
                mess.setMinWidth(400);
                usr.getStylesheets().add(ChatUI.class.getResource("Usr.css").toExternalForm());
                mess.getStylesheets().add(ChatUI.class.getResource("IncomeMesg.css").toExternalForm());
                tmpB.getChildren().addAll(usr, mess);

                vb.getChildren().add(tmpB);
                scrollChat.setContent(vb);
                scrollChat.vvalueProperty().bind((ObservableValue<? extends Number>) vb.heightProperty());
                return;
            }
        }

        VBox vb = new VBox();
        vb.setStyle("-fx-max-width: 520px");
        vb.setStyle("-fx-spacing: 20px");
        if(!peer_lst.isBlank()) {
            Label lb = new Label("Group Members: " + peer_lst);
            lb.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/GMemLB.css").toExternalForm());
            vb.getChildren().add(0, lb);
        }
        ScrollPane scrollChat = new ScrollPane(vb);
        scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollChat.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/ChatArea.css").toExternalForm());
        AnchorPane pane = new AnchorPane(scrollChat);
        pane.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/AnchorPane.css").toExternalForm());
        Tab tab = new Tab(tab_name, pane);
        tabPane.getTabs().add(tab);

        VBox tmpB = new VBox();
        Label usr = new Label(peer_name);
        Label mess = new Label(s);
        mess.setMinWidth(400);
        mess.setWrapText(true);
        usr.getStylesheets().add(ChatUI.class.getResource("Usr.css").toExternalForm());
        mess.getStylesheets().add(ChatUI.class.getResource("IncomeMesg.css").toExternalForm());
        tmpB.getChildren().addAll(usr, mess);

        vb.getChildren().add(tmpB);
        scrollChat.setContent(vb);
        scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollChat.vvalueProperty().bind((ObservableValue<? extends Number>) vb.heightProperty());

    }

    private void genSendingMess(String s) {
        AnchorPane pane = (AnchorPane)tabPane.getSelectionModel().getSelectedItem().getContent();
        ScrollPane scrollChat= (ScrollPane) pane.getChildren().get(0);
        scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox vb = (VBox)scrollChat.getContent();

        Label mess = new Label(s);
        mess.setMinWidth(400);
        mess.setWrapText(true);
        mess.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/SendMesg.css").toExternalForm());
        vb.getChildren().add(mess);
        scrollChat.setContent(vb);
        scrollChat.vvalueProperty().bind((ObservableValue<? extends Number>) vb.heightProperty());
    }


    public void onbtnSendClick(MouseEvent e) throws Exception {
        Button button = (Button) e.getSource();
        String peer = tabPane.getSelectionModel().getSelectedItem().getText();

        if(peer.contains("-")) {
            Customer tmp_cst = null;
            int i;
            for(i = 0; i < cst_lst.size(); i++) {
                System.out.println(peer.substring(1,peer.length()-1));
                if(cst_lst.get(i).getCstID().matches(peer.substring(1,peer.length()-1))) {
                    System.out.println("inside search for customer");
                    tmp_cst = cst_lst.get(i);
                    break;
                }
            }
            if(tmp_cst != null && tmp_cst.getCstStatus()==1) {
                try {
                    System.out.println("inside message for customer");

                    String sendWmsg = AttachTagsMsg.processTextMessage(current.getUsrID(),txtArea.getText());
                    tmp_cst.getoStream().write(encode(sendWmsg));
                    tmp_cst.getoStream().flush();

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

        }
        else {
            if (!(peer.contains("~"))) {
                String host = SendMsg.getIP(peer, peerLst);
                System.out.println("Host :" + tabPane.getSelectionModel().getSelectedItem().getText() + " " + host);
                if (!(txtArea.getText().isBlank() || host == null)) {
                    SendMsg.sendMessage(current, txtArea.getText(), host, TXTPORT);
                }

            } else {
                System.out.println("group mess");
                Tab t = tabPane.getSelectionModel().getSelectedItem();
                AnchorPane pane = (AnchorPane) t.getContent();
                ScrollPane scrollChat = (ScrollPane) pane.getChildren().get(0);
                scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                VBox vb = (VBox) scrollChat.getContent();
                Label groupmem = (Label) vb.getChildren().get(0);

                String[] memlst = groupmem.getText().replace("Group Members: ", "").split(",");
                for (String mem : memlst) {
                    System.out.println(mem);
                    if (!mem.matches(current.getUsrID())) {
                        String host = SendMsg.getIP(mem, peerLst);
                        System.out.println("Group: " + host);
                        if (!(txtArea.getText().isBlank() || host == null)) {
                            User g_current = new User(peer + ":" + current.getUsrID() + ":" + groupmem.getText().replace("Group Members: ", ""), current.getUsrIP(),
                                    current.getUsrPasswd(), current.getUsrPort(), current.getUsrStatus());
                            SendMsg.sendMessage(g_current, txtArea.getText(), host, TXTPORT);
                        }
                    }
                }
            }
        }
        genSendingMess(txtArea.getText());
        //Clear Text Area
        txtArea.setText("");
    }


    public void createPeerTab(User user){
        HBox tmp = new HBox();
        Button nameTag = new Button(user.getUsrID());
        nameTag.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                for(Tab t : tabPane.getTabs()){
                    if(t.getText().matches(user.getUsrID())){
                        tabPane.getSelectionModel().select(t);
                        return;
                    }
                }
                VBox vb = new VBox();
                vb.setStyle("-fx-max-width: 520px");
                vb.setStyle("-fx-spacing: 20px");
                ScrollPane scrollChat = new ScrollPane(vb);
                scrollChat.vvalueProperty().bind((ObservableValue<? extends Number>) vb.heightProperty());
                scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrollChat.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/ChatArea.css").toExternalForm());
                AnchorPane pane = new AnchorPane(scrollChat);
                pane.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/AnchorPane.css").toExternalForm());
                Tab tab = new Tab(user.getUsrID(),pane);
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);

            }
        });

        Circle c1 = new Circle(5);
        nameTag.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/NameTag.css").toExternalForm());
        tmp.getChildren().addAll(nameTag,c1);
        if (user.getUsrStatus() == 1) {
            c1.setFill(Paint.valueOf("#50e09e"));
            HBox.setMargin(c1,new Insets(25,25,20,20));
            vbFriend.getChildren().add(0, tmp);
        }
        else {
            c1.setFill(Paint.valueOf("#bcbcbc"));
            HBox.setMargin(c1,new Insets(25,25,20,20));
            vbFriend.getChildren().add( tmp);
        }
    }

    public void clearPeerTab(){
        vbFriend.getChildren().clear();
    }

    public void onbtnLogoutClicked(MouseEvent e) throws  Exception {
        Button button = (Button) e.getSource();

        // Send notice to server
        String message = "";
        try
        {
            int recvport = SVRPORT;
            //InetAddress server_ip_addr = InetAddress.get;
            socketClient = new Socket(SERVER, recvport);
            // Encode message (user-defined protocol)
            message = AttachTagsMsg.processOfflineStatus(current.getUsrID());
            // Send message to the server
            ObjectOutputStream sender = new ObjectOutputStream(socketClient.getOutputStream());
            sender.writeObject(message); sender.flush();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                socketClient.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
//            try
//            {
//                socket.close();
//            }
//            catch(Exception ex1)
//            {
//
//            }
        }

        ///Broadcast status update to other peer and server
        SendMsg.broadcastStatus(current,peerLst,0);

        try
        {
            //socket.close();
            if(serverSocket!=null) {
                loop = false;
                System.out.println("Close server from logout");
                serverSocket.close();
            }

        }
        catch(Exception ex)
        {
            System.out.println("Catch exception in logout");
            ex.printStackTrace();
        }

        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginUI.fxml"));/* Exception */
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }


    private static Socket Fsocket;
    public void onbtnSendFileclicked(MouseEvent e) throws Exception {
        Button button = (Button) e.getSource();
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if(selectedFile!= null) {
            String path = selectedFile.getAbsolutePath();
            System.out.println(path);
            File myFile = new File(path);
//        byte[] myByteArray = new byte[(int) myFile.length()];

            try {
                String host = SendMsg.getIP(tabPane.getSelectionModel().getSelectedItem().getText(), peerLst);
                //String host = "192.168.43.200";
                if (host != null) {
                    Fsocket = new Socket(host, TXTPORT);
                    // Encode message (user-defined protocol)
                    String message = AttachTagsMsg.processFileMessage(current.getUsrID(),
                            path.substring(path.lastIndexOf("/") + 1));
                    // Send message to the server
                    ObjectOutputStream sender = new ObjectOutputStream(Fsocket.getOutputStream());
                    sender.writeObject(message);
                    sender.flush();
                    System.out.println("314: " + message);
                    // Get acknowledgment from the server
                    ObjectInputStream listener = new ObjectInputStream(Fsocket.getInputStream());
                    message = (String) listener.readObject();
                    System.out.println("Accept or not?" + message);
                    // Close socket
                    //Fsocket.close();

                    if (message.matches(Tags.FILE_ACCEPT_TAG)) {
                        SendMsg.sendMessage(current, myFile, host, FILEPORT);

                    } else {
                        AlertBox alertbox = new AlertBox();
                        alertbox.display("Failed", tabPane.getSelectionModel().getSelectedItem().getText() + " doesn't accept your file.");
                    }

                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                //Closing the socket
                try {
                    Fsocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static Socket Gsocket;
    public static String gmem_list = "";
    public static String grp_name = "";
    public void onbtnGroupChatclicked(MouseEvent e) throws Exception {
        // Open window for user to input group member and group name => check if group name is already used;
        GroupChatBox gb = new GroupChatBox();
        gb.display("Create Group Chat");
        if(!(gmem_list.isBlank()&&grp_name.isBlank())) {
            gmem_list += ","+current.getUsrID();
            String[] memlst = gmem_list.split(",");
            int i;
            boolean found;
            for(String str : memlst) {
                found = false;
                System.out.println("STR: " + str);
                if(str.matches(current.getUsrID()))
                    continue;
                for (i = 0; i < peerLst.size(); i++) {
                    if(str.matches(peerLst.get(i).getUsrID())) {
                        System.out.println("Found matching "+peerLst.get(i).getUsrID());
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    AlertBox a = new AlertBox();
                    a.display("Member not found", str + " not found.");
                    return;
                }
            }
            for (Tab t : tabPane.getTabs()) {
                if (t.getText().matches("~" + grp_name + "~")) {
                    AlertBox a = new AlertBox();
                    a.display("Duplicated Group Name", "Group name ~"+grp_name + "~ already exists.");
                    return;
                }
            }

            VBox vb = new VBox();
            vb.setStyle("-fx-max-width: 520px");
            vb.setStyle("-fx-spacing: 20px");
            ScrollPane scrollChat = new ScrollPane(vb);
            scrollChat.vvalueProperty().bind((ObservableValue<? extends Number>) vb.heightProperty());
            scrollChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollChat.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/ChatArea.css").toExternalForm());
            Label lb = new Label("Group Members: "+gmem_list);
            lb.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/GMemLB.css").toExternalForm());

            vb.getChildren().add(0,lb);
            AnchorPane pane = new AnchorPane(scrollChat);
            pane.getStylesheets().add(ChatUI.class.getResource("../GUI_CSS/AnchorPane.css").toExternalForm());
            Tab tab = new Tab("~"+grp_name+"~",pane);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            //Reset global var to empty string;
            gmem_list = "";
            grp_name = "";
            return;
        }
        AlertBox a = new AlertBox();
        a.display("Blank Value", "Group name or Group Member was left blank.");
        return;

    }



    public void onbtnSettingclicked(MouseEvent e) throws Exception {
        SettingBox s = new SettingBox();

        //System.out.println("res" + s.returnPath());
        //System.out.println(s.returnPath());
        if(!s.returnPath().isBlank() && !s.returnOpacity().isBlank()) {
            File myFile = new File(s.returnPath());
            Image img = new Image(myFile.toURI().toString());
            imgBackground.setImage(img);
            imgBackground.setOpacity(Double.valueOf(s.returnOpacity()));
            imgBackground.setPreserveRatio(false);
        }
        else
            imgBackground.setImage(null);
    }



    public void processWebInputStream(InputStream inputStream) throws IOException {

        //rawIn is a Socket.getInputStream();
        String res = "";

        int len = 0;
        byte[] b = new byte[1024];
        try {
            len = inputStream.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (len != -1) {

            byte rLength = 0;
            int rMaskIndex = 2;
            int rDataStart = 0;
            //b[0] is always text in my case so no need to check;
            byte data = b[1];
            byte op = (byte) 127;
            rLength = (byte) (data & op);

            if (rLength == (byte) 126) rMaskIndex = 4;
            if (rLength == (byte) 127) rMaskIndex = 10;

            byte[] masks = new byte[4];

            int j = 0;
            int i = 0;
            for (i = rMaskIndex; i < (rMaskIndex + 4); i++) {
                masks[j] = b[i];
                j++;
            }

            rDataStart = rMaskIndex + 4;

            int messLen = len - rDataStart;

            byte[] message = new byte[messLen];

            for (i = rDataStart, j = 0; i < len; i++, j++) {
                message[j] = (byte) (b[i] ^ masks[j % 4]);
            }

            System.out.println(new String(message)+"1");

            b = new byte[1024];
            res = new String(message);
            String u = DetachTagsMsg.getTextMessage(res);
            System.out.println(u);
            String[] u_m = u.split("::",2);
            boolean exist = false;
            for(wcount=0;wcount<cst_lst.size();wcount++) {
                if(cst_lst.get(wcount).getCstID().matches(u_m[0])) {
                    exist = true;
                    break;
                }
            }
            System.out.println("after for loop");
            if (exist == false) {
                System.out.println("into loop");
                Customer tmp = new Customer(u_m[0],inputStream,clientSocket.getOutputStream(),1);
                cst_lst.add(0,tmp);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int len = 0;
                        byte[] b = new byte[1024];
                        Customer t = cst_lst.get(0);
                        while(t.getCstStatus()==1) {

                            try {
                                len = inputStream.read(b);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (len != -1) {

                                byte rLength = 0;
                                int rMaskIndex = 2;
                                int rDataStart = 0;
                                //b[0] is always text in my case so no need to check;
                                byte data = b[1];
                                byte op = (byte) 127;
                                rLength = (byte) (data & op);

                                if (rLength == (byte) 126) rMaskIndex = 4;
                                if (rLength == (byte) 127) rMaskIndex = 10;

                                byte[] masks = new byte[4];

                                int j = 0;
                                int i = 0;
                                for (i = rMaskIndex; i < (rMaskIndex + 4); i++) {
                                    masks[j] = b[i];
                                    j++;
                                }

                                rDataStart = rMaskIndex + 4;

                                int messLen = len - rDataStart;

                                byte[] message = new byte[messLen];

                                for (i = rDataStart, j = 0; i < len; i++, j++) {
                                    message[j] = (byte) (b[i] ^ masks[j % 4]);
                                }

                                System.out.println(new String(message));
                                String msg = DetachTagsMsg.getTextMessage(new String(message));

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Current :"+current.getUsrID());
                                        //update application thread
//                                        String tmp = msg.replaceAll("(\\ n)", "\n");
                                        String[] id_mess = msg.split("::", 2);
                                        if(id_mess[1].matches("offline")) {
                                            int i;
                                            for(i=0;i<cst_lst.size();i++) {
                                                if(cst_lst.get(i).getCstID().matches(id_mess[0])) {
                                                    cst_lst.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                        System.out.println("genCommingmess :"+id_mess[0]);
                                        genCommingMess("-"+id_mess[0]+"-", id_mess[1]);
                                    }
                                });
                                b = new byte[1024];
                            }
                        }
                    }
                }).start();
            }

        }

    }




    /* When successfully logged in, start sending request to MainServer
     * to retrieve user list and show that list in vbFriend.upd
     * Update the list and friend status every 1 minutes
     */


    private static ArrayList<Customer> cst_lst = new ArrayList<Customer>();
    private static int wcount;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loop = true;
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);



//================================================Start Listening for Chat Request===================================
        System.out.println("in ra current" + current.getUsrID());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                System.out.println("Inner"+current.getUsrID());
//================================================Start Listening for Customer Request===================================
                if (current.getUsrID().matches("TrioAdmin" + "[0-9]*")) {
                    System.out.println("Admin login");
                    // CustomerService custSvr = new CustomerService();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int portNumber = 40000;


                            try {
                                serverWeb = new ServerSocket(portNumber);
                            } catch (IOException exception) {
                                throw new IllegalStateException("Could not create web server", exception);
                            }


                            while (true) {
                                try {
                                    clientSocket = serverWeb.accept(); //waits until a client connects
                                } catch (IOException waitException) {
                                    throw new IllegalStateException("Could not wait for client connection", waitException);
                                }

                                InputStream inputStream;
                                try {
                                    inputStream = clientSocket.getInputStream();
                                } catch (IOException inputStreamException) {
                                    throw new IllegalStateException("Could not connect to client input stream", inputStreamException);
                                }

                                OutputStream outputStream;
                                try {
                                    outputStream = clientSocket.getOutputStream();
                                } catch (IOException inputStreamException) {
                                    throw new IllegalStateException("Could not connect to client input stream", inputStreamException);
                                }

                                try {
                                    CustomerService.doHandShakeToInitializeWebSocketConnection(inputStream, outputStream);
                                } catch (UnsupportedEncodingException handShakeException) {
                                    throw new IllegalStateException("Could not connect to client input stream", handShakeException);
                                }

                                try {
                                    processWebInputStream(inputStream);
                                } catch (IOException printException) {
                                    throw new IllegalStateException("Could not connect to client input stream", printException);
                                }
                            }

                        }
                    }).start();
                }

//================================================Start Listening for Peer Chat===================================

                    int port = TXTPORT;
                    serverSocket = new ServerSocket(port);
                    System.out.println("Server Started and listening to the port " + port);

                    //Listener is running always. This is done using this while(true) loop
                    while (loop) {
                        //Reading the message from the peer
                        socket = serverSocket.accept();


                        InputStream i_stream = socket.getInputStream();
                        ObjectInputStream receiver = null;


                        String clientRequest = "";
                        String clientMsg = "";


                        receiver = new ObjectInputStream(i_stream);
                        clientRequest = (String) receiver.readObject();
                        System.out.println(clientRequest);


                        System.out.println("Receive mess from web: " + clientMsg);
                        System.out.println("Receive mess from peer: " + clientRequest);

                        // Analyse message
                        // Case 1: Text Message sent from peer
                        final String peerRequest = DetachTagsMsg.getTextMessage(clientRequest);

                        System.out.println("Detach mess: " + peerRequest);
                        if (peerRequest != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("Current :" + current.getUsrID());
                                    //update application thread
                                    String tmp = peerRequest.replaceAll("(\\ n)", "\n");
                                    String[] id_mess = tmp.split("::", 2);
                                    System.out.println("genCommingmess :" + id_mess[0]);
                                    genCommingMess(id_mess[0], id_mess[1]);
                                }
                            });
                        }

                        // Case 2: Request sending file from peer
                        final String fileRequest = DetachTagsMsg.checkFile(clientRequest);
                        System.out.println("Receive File : " + fileRequest);
                        if (fileRequest != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("Inside case 2");
                                    //update application thread
                                    String tmp = fileRequest.replaceAll("\\ n", "\n");
                                    String[] id_fname = tmp.split("::", 2);
                                    AcceptFileBox afbox = new AcceptFileBox();
                                    afbox.display("Accept File", id_fname, socket, current, peerLst);
                                    ////////////////////////////////////////////////Hien cua so thong bao///////////////////////////////////////////////
                                }
                            });
                        }

                        // Case 3: Status update
                        String offID = DetachTagsMsg.getDiedAccount(clientRequest);
                        // Search for ID in peerlist

                        if (offID != null) {
                            for (User u : peerLst) {
                                if (u.getUsrID().matches(offID)) {
                                    u.setUsrStatus(0);
                                    u.setUsrIP("");
                                    break;
                                }
                            }
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    clearPeerTab();
                                    for (User u : peerLst) {
                                        if (!u.getUsrID().matches(current.getUsrID())) {
                                            createPeerTab(u);
                                        }
                                    }
                                }
                            });
                        }

                        User onID = DetachTagsMsg.getOnlAccount(clientRequest);
                        // Search for ID in peerlist
                        if (onID != null) {
                            System.out.println("onID: " + onID.getUsrIP());
                            int i;
                            boolean found = false;
                            for (i = 0; i < peerLst.size(); i++) {
                                if (peerLst.get(i).getUsrID().matches(onID.getUsrID())) {
                                    peerLst.get(i).setUsrStatus(1);
                                    peerLst.get(i).setUsrIP(onID.getUsrIP());
                                    System.out.println("Listen to status update => update peerlst ?!");
                                    found = true;
                                    break;
                                }
                                //peerLst.add(onID);
                            }
                            if (!found) {
                                peerLst.add(onID);
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    clearPeerTab();
                                    for (User u : peerLst) {
                                        if (!u.getUsrID().matches(current.getUsrID()))
                                            createPeerTab(u);
                                    }
                                }
                            });
                        }

                    }
                } catch (Exception e) {
                    System.out.println("Catch exception in receive");
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        if (serverSocket != null) {
                            System.out.println("Server Socker is closed !");
                            serverSocket.close();
                        }

                    } catch (Exception e) {

                    }
                }
            }
        }).start();


        System.out.println("Current here" + current.getUsrID());



    }
}



