package login_register;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Class.*;
import Protocol.*;
import java.sql.SQLException;




public class Server {
    /* Server save all of registered user data in User_Database file
     * UserID UserIP UserPwd UserPort UserStatus
     */

    private String ip;
    private ArrayList<User> usr_lst = null;
    private ServerSocket server;
    private Socket connection;
    private ObjectOutputStream sender;
    private ObjectInputStream receiver;
    private User admin = new User();
    private String ad;
    public boolean isStop = false;
    public boolean isOffline = false;
    public static ArrayList<Socket> arrS = new ArrayList<Socket>();

    public final static int SERVER_PORT = 44000;

    public Server(int port) throws Exception{
        server = new ServerSocket(port);
        usr_lst = new ArrayList<User>();
        (new WaitforRequest()).start();
    }

    //Waiting for connection request
    private int listen() throws Exception{ // return true if it's account verification or account register

        System.out.println("Connecting to client at port" + SERVER_PORT);
        connection = server.accept();



        InputStream i_stream = connection.getInputStream();



        String clientRequest = "";
        String clientMsg = "";
        BufferedReader buf_reader = null;

        try {
            receiver = new ObjectInputStream(i_stream);
            clientRequest = (String) receiver.readObject();
            System.out.println(clientRequest);
        } catch (Exception e) {
            InputStreamReader is_reader = new InputStreamReader(i_stream);
            buf_reader = new BufferedReader(is_reader);
            clientMsg = buf_reader.readLine();
        }


        System.out.println("Start classify client request.");



        System.out.println("client Msg" + clientMsg);
        System.out.println("client Msg" + clientMsg);
        System.out.println("client Msg" + clientMsg);
        System.out.println("client Msg" + clientMsg);


        if (clientMsg != null) {        // Request from server
//            arrS.add(connection);
            String[] requestParam = clientMsg.split(" ");
            String path = requestParam[0];
            if (path.matches("/")) {
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.println("HTTP/1.1 200 OK");
                out.println("Content-type: text/html");
                out.println("Set-Cookie: admin="+ad);
                out.println("\r\n");


                File file = new File("/home/fouriv/Documents/2_Year3/Computer_Network/Lab/Assigment/Server/src/login_register/index.html");
                if (!file.exists()) {
                    out.write("HTTP 404"); // the file does not exists
                }
                FileReader fr = new FileReader(file);
                BufferedReader bfr = new BufferedReader(fr);
                String line;
                while ((line = bfr.readLine()) != null) {
                    out.write(line);
                }

                //In this line I used out.println("full html code"); but I'd like a simpler way where it can search for the html file in the directory and send it.
                out.flush();
                bfr.close();

                out.close();
            }
            System.out.println("path here" +path);
            if (path.contains("/msg")) {
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.println("HTTP/1.1 204 No Content");
                out.println("Set-Cookie: admin="+ad);
                out.println("\r\n");


                File file = new File("/home/fouriv/Documents/2_Year3/Computer_Network/Lab/Assigment/Server/src/login_register/javascript.js");
                if (!file.exists()) {
                    out.write("HTTP 404"); // the file does not exists
                }
                FileReader fr = new FileReader(file);
                BufferedReader bfr = new BufferedReader(fr);
                String line;
                while ((line = bfr.readLine()) != null) {
                    out.write(line);
                }

                //In this line I used out.println("full html code"); but I'd like a simpler way where it can search for the html file in the directory and send it.
                out.flush();
                bfr.close();

                out.close();
            }

//            System.out.println(path.contains("custId="));
//
//            if (path.contains("/?custId=")&&path.contains("custMsg=")) {    // Handle client message and forward to admin
//                System.out.println("message");
//                PrintWriter out = new PrintWriter(connection.getOutputStream());
//                out.println("HTTP/1.1 204 No Content");
//                out.println("Set-Cookie: admin="+String.valueOf(admin.getUsrStatus()));
//                out.println("\r\n");
//
//                //In this line I used out.println("full html code"); but I'd like a simpler way where it can search for the html file in the directory and send it.
//                out.flush();
//                out.close();
//            }

        }

//        System.out.println("Start classify client request.");
        // Classify request from client
        // CASE 1 : Client request account verify
//
        User acc_reg = DetachTagsMsg.getAccountRegistration(clientRequest);
        User acc_info = DetachTagsMsg.getAccountInformation(clientRequest);
        String offID = DetachTagsMsg.getDiedAccount(clientRequest);
        String username = DetachTagsMsg.getNameRequestChat(clientRequest);


        if (acc_info != null || clientRequest.matches(Tags.UPDATE_PEER_LST_TAG)) {
            if(acc_info.getUsrID().matches("TrioAdmin"+"[0-9]*")) { // Check for admin status update
                admin = acc_info;
                ad = admin.getUsrIP();
            }

            System.out.println(acc_info.getUsrID() + " Login!");

            PreparedStatement st;
            ResultSet rs;
            String query = "SELECT * FROM `users` WHERE `username` = ? AND `password` = ?";

            try {
                // Check username and password
                st = My_CNX.getConnection().prepareStatement(query);

                st.setString(1, acc_info.getUsrID());
                st.setString(2, acc_info.getUsrPasswd());
                rs = st.executeQuery();

                if (rs.next()){ // valid username and password
                    ArrayList<String> username_data = new ArrayList<String>();
                    ArrayList<String> ip_data = new ArrayList<String>();
                    ArrayList<Integer> port_data = new ArrayList<Integer>();
                    ArrayList<Integer> status_data = new ArrayList<Integer>();

                    String query_friend = "SELECT `username`, `IP`, `port`, `status` FROM `users`";
                    st = My_CNX.getConnection().prepareStatement(query_friend);
                    rs = st.executeQuery();

                    while(rs.next()){
                        username_data.add(rs.getString("username"));
                        ip_data.add(rs.getString("IP"));
                        port_data.add(rs.getInt("port"));
                        status_data.add(rs.getInt("status"));
                    }

                    usr_lst = new ArrayList<User>();
                    int i;
                    for(i = 0; i < username_data.size(); i++) {
                        User tmp = new User();
                        tmp.setUsrID(username_data.get(i));
                        tmp.setUsrIP(ip_data.get(i));
                        tmp.setUsrPort(port_data.get(i));
                        tmp.setUsrStatus(status_data.get(i));
                        usr_lst.add(tmp);
                    }

                    // update: set port, set ip, update status
                    String update_query = "UPDATE `users` SET `IP`= ?,`port`= ?,`status`= ? WHERE `username` = ?";
                    st = My_CNX.getConnection().prepareStatement(update_query);

                    st.setString(1, connection.getInetAddress().getHostAddress());  // Get IP
                    st.setString(2, String.valueOf(connection.getPort()));
                    st.setString(3, String.valueOf(1));     // Set status = 1
                    st.setString(4, acc_info.getUsrID());
                    st.execute();

                    return 1;
                }else{
                    return 2;
                }
            } catch (SQLException ex) {
                System.out.println("Error SQL");
            }
        }


        // CASE 2 : Client send an offline status
//        String offID = DetachTagsMsg.getDiedAccount(clientRequest);
        if (offID != null) {
            if(offID.matches("TrioAdmin"+"[0-9]*")){
                System.out.println("Admin has logged out!");
                admin.setUsrStatus(0);
                ad = "0";
            }
            System.out.println( offID + " Logout!");
            PreparedStatement st;
            String updateStatusQuery = "UPDATE `users` SET `status`= 0 WHERE `username` = ?";

            try {
                st = My_CNX.getConnection().prepareStatement(updateStatusQuery);
                st.setString(1, offID);
                st.execute();
            } catch (SQLException ex) {
                System.out.println("Error SQL");
            }
            return 0;
        }

        // Case 3: Client register account
//        User acc_reg = DetachTagsMsg.getAccountRegistration(clientRequest);
        if (acc_reg != null){

            System.out.println(acc_reg.getUsrID() + " Register!");
            PreparedStatement st;
            ResultSet rs;

            String user_check_query = "SELECT * FROM `users` WHERE `username` = ?";   // query for check username

            try {
                st = My_CNX.getConnection().prepareStatement(user_check_query);

                st.setString(1, acc_reg.getUsrID());
                rs = st.executeQuery();

                if (rs.next()) {        // If username have already existed
                    return 3;

                }else{
                    String insert_query = "INSERT INTO `users`(`username`, `password`) VALUES (?,?)";

                    //Insert username, password to database
                    st = My_CNX.getConnection().prepareStatement(insert_query);

                    st.setString(1, acc_reg.getUsrID());
                    st.setString(2, acc_reg.getUsrPasswd());
                    st.execute();

                    return 4;
                }

            } catch (SQLException ex) {
                System.out.println("Error SQL");
            }
        }

        if (username != null){

            System.out.println("Get IP of " + username);
            PreparedStatement st;
            ResultSet rs;

            String find_username = "SELECT * FROM `users` WHERE `username` = ?";

            try {
                System.out.println("Get Connection done");
                st = My_CNX.getConnection().prepareStatement(find_username);
                st.setString(1, username);
                rs = st.executeQuery();
                rs.next();
                ip = rs.getString("IP");
                System.out.println("Ip of " + username + " " + ip);
                return 5;

            } catch (SQLException ex) {
                System.out.println("Error SQL");
            }


        }


        return -1;
    }

    public class WaitforRequest extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (!isStop) {
                    String serverReply = "";
                    switch (listen()) {
                        case -1:
                            System.out.println("Don't exist request");
                            break;
                        case 0:
                            break;
                        case 1:
                            serverReply = AttachTagsMsg.processSendChatIP(usr_lst);
                            //Send message to client
                            sender = new ObjectOutputStream(connection.getOutputStream());
                            sender.writeObject(serverReply);
                            sender.flush();
                            //sender.close();
                            break;
                        case 2:
                            serverReply = Tags.ACC_DENY_TAG;
                            //Send message to client
                            sender = new ObjectOutputStream(connection.getOutputStream());
                            sender.writeObject(serverReply);
                            sender.flush();
                            //sender.close();
                            break;
                        case 3:
                            serverReply = Tags.REG_FAIL_TAG;
                            //Send message to client
                            sender = new ObjectOutputStream(connection.getOutputStream());
                            sender.writeObject(serverReply);
                            sender.flush();
                            //sender.close();
                            break;
                        case 4:
                            serverReply = Tags.REG_SUCCESS_TAG;
                            //Send message to client
                            sender = new ObjectOutputStream(connection.getOutputStream());
                            sender.writeObject(serverReply);
                            sender.flush();
                            //sender.close();
                            break;
                        case 5:
                            serverReply = ip;
                            //Send message to client
                            sender = new ObjectOutputStream(connection.getOutputStream());
                            sender.writeObject(serverReply);
                            sender.flush();
                            //sender.close();
                            break;
                        default:
                            break;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server MainServer = new Server(SERVER_PORT);
    }
}
