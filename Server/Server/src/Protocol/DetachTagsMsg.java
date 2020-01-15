package Protocol;

import Class.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DetachTagsMsg {

    public static User getAccountInformation(String message) {
        if (find_account.matcher(message).matches()) {

            Document document = convertStringToXML(message);
            String name = document.getElementsByTagName("USR_ID").item(0).getTextContent();
            String pwd = document.getElementsByTagName("USR_PWD").item(0).getTextContent();
            String ip = document.getElementsByTagName("USR_IP").item(0).getTextContent();
            Integer port = Integer.parseInt(document.getElementsByTagName("USR_PORT").item(0).getTextContent());
            Integer status = 1;
            return new User(name,ip,pwd,port,status);
        } else
            return null;
    }

    public static User getAccountRegistration(String message) {
        if (new_account.matcher(message).matches()) {
            Document document = convertStringToXML(message);
            String name = document.getElementsByTagName("USR_ID").item(0).getTextContent();
            String pwd = document.getElementsByTagName("USR_PWD").item(0).getTextContent();
            Integer status = 1;
            return new User(name,"",pwd,0,status);
        } else
            return null;
    }

    public static String getDiedAccount(String message) {
        if (status_off.matcher(message).matches()) {
            Document document = convertStringToXML(message);
            String ID = document.getElementsByTagName("SESSION_OFF").item(0).getTextContent();
            return ID;
        }
        return null;
    }

    public static User getOnlAccount(String message) {
        if (status_onl.matcher(message).matches()) {
            Document document = convertStringToXML(message);

            String name = document.getElementsByTagName("USR_ID").item(0).getTextContent();

            String pwd = document.getElementsByTagName("USR_PWD").item(0).getTextContent();

            String ip = document.getElementsByTagName("USR_IP").item(0).getTextContent();

            Integer port = Integer.parseInt(document.getElementsByTagName("USR_PORT").item(0).getTextContent());

            Integer status = 1;

            return new User(name,ip,pwd,port,status);
        }
        return null;
    }

    public static ArrayList<User> getSendIP(String message) {
        if (send_IP.matcher(message).matches()) {
            ArrayList<User> account_list = new ArrayList<User>();

            Document document = convertStringToXML(message);
            NodeList node_list = document.getElementsByTagName("CHAT_IP");

            for(int i = 0; i < node_list.getLength(); i = i + 1) {

                Node node = node_list.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String name = element.getElementsByTagName("USR_ID").item(0).getTextContent();
                    String host = element.getElementsByTagName("USR_IP").item(0).getTextContent();
                    Integer port = Integer.parseInt(element.getElementsByTagName("USR_PORT").item(0).getTextContent());
                    Integer stat = Integer.parseInt(element.getElementsByTagName("USR_STAT").item(0).getTextContent());
                    User usr = new User(name, host, "", port,stat);

                    account_list.add(usr);
                }
            }

            return account_list;
        } else
            return null;
    }

    public static String getTextMessage(String message) {
        if (find_message.matcher(message).matches()) {
            Document document = convertStringToXML(message);
            String ID = document.getElementsByTagName("USR_ID").item(0).getTextContent();
            String msg = document.getElementsByTagName("TXT_MSG").item(0).getTextContent();
            return ID+"::"+msg;
        } else
            return null;
    }



    public static String getNameRequestChat(String msg) {
        if (check_request.matcher(msg).matches()) {
            Document document = convertStringToXML(msg);
            String ID = document.getElementsByTagName("USR_ID").item(0).getTextContent();
            return ID;
        } else
            return null;
    }

    public static boolean checkFile(String name) {
        if (check_file.matcher(name).matches())
            return true;
        else
            return false;
    }

    public static boolean checkFeedBack(String message) {
        Pattern feed_back = Pattern.compile(Tags.CHAT_ACCEPT_TAG);
        if (feed_back.matcher(message).matches())
            return true;
        else
            return false;
    }

    private static Document convertStringToXML(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document document = builder.parse(
                    new InputSource(new StringReader(xmlString))
            );

            //Normalize xml structure
            document.normalize();

            return document;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static Pattern find_account = Pattern.compile(
            Tags.ACC_VERIF_OPEN_TAG + Tags.USR_ID_OPEN_TAG + ".*" + Tags.USR_ID_CLOSE_TAG
                                    + Tags.USR_PWD_OPEN_TAG + ".*" + Tags.USR_PWD_CLOSE_TAG
                                    + Tags.USR_IP_OPEN_TAG + ".*" + Tags.USR_IP_CLOSE_TAG
                                    + Tags.USR_PORT_OPEN_TAG + ".*" + Tags.USR_PORT_CLOSE_TAG
                                    + Tags.ACC_VERIF_CLOSE_TAG
    );

    private static Pattern new_account = Pattern.compile(
            Tags.ACC_REGIS_OPEN_TAG + Tags.USR_ID_OPEN_TAG + ".*" + Tags.USR_ID_CLOSE_TAG
                                    + Tags.USR_PWD_OPEN_TAG + ".*" + Tags.USR_PWD_CLOSE_TAG
                                        + Tags.ACC_REGIS_CLOSE_TAG
    );

    private static Pattern find_message = Pattern.compile(
            Tags.CHAT_MSG_OPEN_TAG+ ".*" + Tags.CHAT_MSG_CLOSE_TAG
    );

    private static Pattern check_request = Pattern.compile(
            Tags.CHAT_REQ_OPEN_TAG + Tags.USR_ID_OPEN_TAG + ".*" + Tags.USR_ID_CLOSE_TAG + Tags.CHAT_REQ_CLOSE_TAG
    );

    private static Pattern send_IP = Pattern.compile(
            Tags.CHAT_IP_OPEN_TAG + ".*" + Tags.CHAT_IP_CLOSE_TAG
    );

    private static Pattern check_file = Pattern.compile(
            Tags.CHAT_MSG_OPEN_TAG + Tags.USR_IP_OPEN_TAG + ".*" + Tags.USR_IP_CLOSE_TAG
                                    + Tags.FILE_MSG_OPEN_TAG + ".*" + Tags.FILE_MSG_CLOSE_TAG
                                    + Tags. CHAT_MSG_CLOSE_TAG
    );

    private static Pattern status_off = Pattern.compile(
            Tags.SESSION_OFF_OPEN_TAG + Tags.USR_ID_OPEN_TAG + ".*" + Tags.USR_ID_CLOSE_TAG
                                        + Tags.SESSION_OFF_CLOSE_TAG
    );

    private static Pattern status_onl = Pattern.compile(
            Tags.SESSION_ON_OPEN_TAG + Tags.USR_ID_OPEN_TAG + ".*" + Tags.USR_ID_CLOSE_TAG
                    + Tags.SESSION_ON_CLOSE_TAG
    );

}