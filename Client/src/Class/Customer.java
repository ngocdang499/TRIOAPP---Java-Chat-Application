package Class;

import java.io.InputStream;
import java.io.OutputStream;

public class Customer {
    private String cstID;
    private InputStream iStream;
    private OutputStream oStream;
    private int cstStatus;

    public Customer()
    {
        cstID = "";
        cstStatus = 0;
    }

    public Customer(String ID, InputStream is, OutputStream os, int Status)
    {
        cstID = ID;
        iStream = is;
        oStream = os;
        cstStatus = Status;
    }

    public void setCstID(String _ID) {
        cstID = _ID;
    }

    public String getCstID() {
        return cstID;
    }

    public void setiStream(InputStream is) {
        iStream=is;
    }

    public InputStream getiStream() {
        return iStream;
    }

    public void setoStream(OutputStream os) {
        oStream = os;
    }

    public OutputStream getoStream() {
        return oStream;
    }

    public void setCstStatus(int _stat) {
        cstStatus = _stat;
    }

    public int getCstStatus() {
        return cstStatus;
    }
}
