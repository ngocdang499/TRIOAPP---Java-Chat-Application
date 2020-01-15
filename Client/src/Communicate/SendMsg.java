package Communicate;
import Class.User;
import Protocol.AttachTagsMsg;
import Protocol.Tags;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SendMsg {
    private static String SERVER;
    private static Integer TXTPORT = 50000;
    private static Integer FILEPORT = 60000;
    private static Integer SVRPORT = 44000;
    private static Socket file_Socket;

    private static Socket socketClient;
    public static String getIP(String peerName, ArrayList<User> peerlst) {
        String host = "";
        // Send request ID verification to server
        for (User u: peerlst) {
            if(u.getUsrID().matches(peerName) && u.getUsrStatus()==1) {
                return u.getUsrIP();
            }
        }
        return null;
    }

    public static void broadcastStatus(User current,ArrayList<User> peerList, int status) {
        // Send notice to peer
        for(User u : peerList) {
            if(u.getUsrStatus() == 1 && !u.getUsrID().matches(current.getUsrID())) {
                System.out.println("Send Message");
                sendMessage(current,status,u.getUsrIP(),u.getUsrPort());
            }
        }
    }


    private static Socket socket;

    public static void sendMessage(User current, Object sendObj, String host, Integer port) {
        // Case 1: Send text message
        if(sendObj instanceof String)    {
            System.out.println("sys");
            // Display message
            String msg = (String)sendObj;
            String finalmess = "";
            // Send mess to Other peer
            //========================================================================================
            try
            {
                int recvport = port;
                socket = new Socket(host,recvport); ;

                //Case 1.1: Accept File
                if(msg.matches(Tags.FILE_ACCEPT_TAG)) {
                    finalmess = Tags.FILE_ACCEPT_TAG;
                }
                else if(msg.matches(Tags.FILE_REFUSE_TAG)) {
                    finalmess = Tags.FILE_REFUSE_TAG;
                }
                else {


                    if (msg.contains("\n")) {
                        msg = msg.replaceAll("\n", "\\ n");
                    }

                    finalmess = AttachTagsMsg.processTextMessage(current.getUsrID(), msg);
                }
                //Send the message to peer
                System.out.println("Send confirm/txt mess");
                ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
                sender.writeObject(finalmess);
                sender.flush();
                sender.close();
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
                    socket.close();
                    return;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        // Case 2: Sending file
        if(sendObj instanceof File) {
            File sendFile = (File)sendObj;
            FileClient fc = new FileClient(host,port,sendFile.getAbsolutePath());
            // Gen notice ==============================================================================================
        }

        // Case 3: Sending notice about change in status
        if(sendObj instanceof Integer) {
            System.out.println("case3: SendMsg.sendMessage => status");
            String msg = "";
            Integer status = (Integer)sendObj;
            try
            {
                int recvport = TXTPORT;
                System.out.println("case3: SendMsg.sendMessage => status => Port: " + recvport);
                file_Socket = new Socket(host,recvport); ;

                if(status==0)
                    msg = AttachTagsMsg.processOfflineStatus(current.getUsrID());
                else {
                    msg = AttachTagsMsg.processOnlineStatus(current.getUsrID(), current.getUsrIP());
                    System.out.println(current.getUsrIP());
                }

                //Send the message to peer
                ObjectOutputStream sender = new ObjectOutputStream(file_Socket.getOutputStream());
                sender.writeObject(msg);
                sender.flush();
                sender.close();
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
                    file_Socket.close();
                    return;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
