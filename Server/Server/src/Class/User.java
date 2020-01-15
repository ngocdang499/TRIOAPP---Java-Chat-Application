/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

/**
 *
 * @author Alan Walker
 */
public class User {

    private String usrID;
    private String usrIP;
    private String usrPasswd;
    private int usrPort;
    private int usrStatus;

    public User()
    {
        usrID = "";
        usrPasswd = "";
        usrIP = "";
        usrPort = 0;
        usrStatus = 0;
    }

    public User(String ID, String IP, String Passwd, int Port, int Status)
    {
        usrID = ID;
        usrPasswd = Passwd;
        usrIP = IP;
        usrPort = Port;
        usrStatus = Status;
    }

    public void setUsrID(String _ID) {
        usrID = _ID;
    }

    public String getUsrID() {
        return usrID;
    }

    public void setUsrIP(String _IP) {
        usrIP = _IP;
    }

    public String getUsrIP() {
        return usrIP;
    }

    public void setUsrPasswd(String _Passwd) {
        usrPasswd = _Passwd;
    }

    public String getUsrPasswd() {
        return usrPasswd;
    }

    public void setUsrPort(int _port) {
        usrPort = _port;
    }

    public int getUsrPort() {
        return usrPort;
    }

    public void setUsrStatus(int _stat) {
        usrStatus = _stat;
    }

    public int getUsrStatus() {
        return usrStatus;
    }


}

