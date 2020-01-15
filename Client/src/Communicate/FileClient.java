package Communicate;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileClient {

    private Socket s;

    public FileClient(String host, int port, String filepath) {
        try {
            s = new Socket(host, port);
            sendFile(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFile(String file) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        int count = 0;
        while ((count = fis.read(buffer)) > 0) {
            dos.write(buffer,0,count);
            System.out.println("Finish");
        }

        System.out.println("Finish");

        fis.close();
        dos.close();

    }

}
