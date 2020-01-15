package Protocol;
import Class.User;

import java.util.ArrayList;

/* Every message exchanged between peers or between client and server
 * will be processed by attaching corresponding tags defining the actions
 * or request need to be performed on that message.
 */

public class AttachTagsMsg {

    // Process message between client and server
    public static String processAccVerification(String _ID, String _Passwd, String _IP, String _Port) {
        return Tags.ACC_VERIF_OPEN_TAG + Tags.USR_ID_OPEN_TAG + _ID + Tags.USR_ID_CLOSE_TAG
                                        + Tags.USR_PWD_OPEN_TAG + _Passwd + Tags.USR_PWD_CLOSE_TAG
                                        + Tags.USR_IP_OPEN_TAG + _IP + Tags.USR_IP_CLOSE_TAG
                                        + Tags.USR_PORT_OPEN_TAG + _Port + Tags.USR_PORT_CLOSE_TAG
                                        + Tags.ACC_VERIF_CLOSE_TAG;
    }

    public static String processAccRegistration(String _ID, String _Passwd) {
        return Tags.ACC_REGIS_OPEN_TAG + Tags.USR_ID_OPEN_TAG + _ID + Tags.USR_ID_CLOSE_TAG
                + Tags.USR_PWD_OPEN_TAG + _Passwd + Tags.USR_PWD_CLOSE_TAG
                + Tags.ACC_REGIS_CLOSE_TAG;
    }

    public static String processOnlineStatus(String _ID, String _IP) {
        return Tags.SESSION_ON_OPEN_TAG + Tags.USR_ID_OPEN_TAG + _ID + Tags.USR_ID_CLOSE_TAG
                                        + Tags.USR_IP_OPEN_TAG + _IP + Tags.USR_IP_CLOSE_TAG
                                        + Tags.SESSION_ON_CLOSE_TAG;
    }

    public static String processOfflineStatus(String _ID) {
        return Tags.SESSION_OFF_OPEN_TAG + Tags.USR_ID_OPEN_TAG + _ID + Tags.USR_ID_CLOSE_TAG
                                        + Tags.SESSION_OFF_CLOSE_TAG;
    }

    public static String processRequestChatIP(String usr) {
        return Tags.CHAT_REQ_OPEN_TAG + Tags.USR_ID_OPEN_TAG + usr + Tags.USR_ID_CLOSE_TAG + Tags.CHAT_REQ_CLOSE_TAG;
    }

    public static String processSendChatIP(ArrayList<User> usrlist) {
        String msg = Tags.CHAT_IP_OPEN_TAG;
        for (User user : usrlist) {
            msg += Tags.USR_ID_OPEN_TAG + user.getUsrID() + Tags.USR_ID_CLOSE_TAG
                    + Tags.USR_IP_OPEN_TAG + user.getUsrIP() + Tags.USR_IP_CLOSE_TAG
                    + Tags.USR_PORT_OPEN_TAG + user.getUsrPort() + Tags.USR_PORT_CLOSE_TAG
                    + Tags.USR_STAT_OPEN_TAG + user.getUsrStatus() + Tags.USR_STAT_CLOSE_TAG
            ;
        }
        return msg += Tags.CHAT_IP_CLOSE_TAG;
    }

    //Chat between peers
    public static String processTextMessage(String user,String txtMsg) {
        // Find pattern with '<' and '>' --replace--> '<<' '>>'
        txtMsg = txtMsg.replaceAll("<","<<");
        txtMsg = txtMsg.replaceAll(">",">>");
        return Tags.CHAT_MSG_OPEN_TAG + Tags.USR_ID_OPEN_TAG + user + Tags.USR_ID_CLOSE_TAG
                                        + Tags.TEXT_MSG_OPEN_TAG + txtMsg + Tags.TEXT_MSG_CLOSE_TAG
                                        + Tags. CHAT_MSG_CLOSE_TAG;
    }

    public static String processFileMessage(String user, String fname) {
        return Tags.CHAT_MSG_OPEN_TAG + Tags.USR_ID_OPEN_TAG + user + Tags.USR_ID_CLOSE_TAG
                                        + Tags.FILE_MSG_OPEN_TAG + fname + Tags.FILE_MSG_CLOSE_TAG
                                        + Tags. CHAT_MSG_CLOSE_TAG;

    }





}

