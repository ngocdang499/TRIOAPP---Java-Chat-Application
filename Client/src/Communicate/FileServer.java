package Communicate;

import Server.AlertBox;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {

    private ServerSocket ss;
    private String savepath = "";
    private boolean complete = false;

    public FileServer(int port, String path) {
        try {
            complete = true;
            ss = new ServerSocket(port);
            savepath = path;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        while (complete) {
            try {
                Socket clientSock = ss.accept();
                saveFile(clientSock, savepath);

                //ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                //Closing the socket
                try
                {
                    System.out.println("Close Server File");
                    ss.close();

                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    private void saveFile(Socket clientSock, String savepath) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream(savepath);
        byte[] buffer = new byte[4096];

        //int filesize = 15123; // Send file size in separate msg
        int read = 0;
        int totalRead = 0;
        //int remaining = filesize;
        while((read = dis.read(buffer)) > 0) {
            totalRead += read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }

        fos.close();
        dis.close();
        complete = false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AlertBox alert = new AlertBox();
                alert.display("Download Completed", "Your file have been saved at\n" + savepath);
            }
        });
    }

}
