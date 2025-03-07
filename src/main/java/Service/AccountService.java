package Service;
import Model.Account;
import DAO.SocialMediaDAO;



public class AccountService {
    SocialMediaDAO socialMediaDAO;

    public AccountService() {
        socialMediaDAO = new SocialMediaDAO();
    }

    public AccountService(SocialMediaDAO socialMediaDAO) {
        this.socialMediaDAO = socialMediaDAO;
    }

    public Account addAccount(Account account) {
        return socialMediaDAO.insertAccount(account);
    }

    public Account checkAccount(Account account) {
        return socialMediaDAO.getAccountInfo(account);
    }

    





}





