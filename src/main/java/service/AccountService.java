package service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import domain.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

// todo: error handling
// todo: handle isActive property
public class AccountService extends AbstractService {

    private final AccountDao accountDao;
    private final UserDao userDao;

    public AccountService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    public void transfer(long accNumberSource, long accNumberDest, BigDecimal money) {
        log(Level.INFO, "Start transfer");
        Account source = accountDao.find(accNumberSource);
        Account dest = accountDao.find(accNumberDest);
        source.dec(money);
        dest.inc(money);
        accountDao.save(source);
        accountDao.save(dest);
    }

    public Account find(long accNumber) {
        return accountDao.find(accNumber);
    }

    public Account persist(Account account) {
        return accountDao.persist(account);
    }

    public List<Account> findAccountsByUserPhone(String phone) {
        User user = userDao.findUserByPhone(phone);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getAccounts()
                .stream()
                .map(accountDao::find)
                .collect(Collectors.toList());
    }
}
