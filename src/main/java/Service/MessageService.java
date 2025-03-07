package Service;
import Model.Message;
import DAO.SocialMediaDAO;

import java.util.*;

public class MessageService {
    SocialMediaDAO socialMediaDAO;

    public MessageService() {
        socialMediaDAO = new SocialMediaDAO();
    }

    public MessageService(SocialMediaDAO socialMediaDAO) {
        this.socialMediaDAO = socialMediaDAO;
    }

    public Message createMsg(Message msg) {
        return socialMediaDAO.createMessage(msg);
    }

    public List<Message> getAllMsgs() {
        return socialMediaDAO.getAllMessages();
    }

    public Message getMsgById(int id) {
        return socialMediaDAO.getMessageById(id);
    }
    

    public Message deleteMessage(int id) {
        return socialMediaDAO.deleteMessageById(id);
    }
    
    public Message updateMessage(int id, String newMessageText) {
        return socialMediaDAO.updateMessageById(id, newMessageText);
    }
    
    public List<Message> getMessagesByAccountId(int accountId) {
        return socialMediaDAO.getMessagesByAccountId(accountId);
    }
    

}
