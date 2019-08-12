package service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import domain.User;
import exceptions.AccountNotFoundException;
import exceptions.NotEnoughMoneyException;

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

        // todo: move validations in separate methods
        if (source == null || dest == null) {
            throw new AccountNotFoundException();
        }

        BigDecimal newSourceMoney = source.getMoney().subtract(money);

        if (newSourceMoney.compareTo(BigDecimal.valueOf(0L)) >= 0) {
            // todo: should be in one transaction
            source.dec(money);
            dest.inc(money);
            accountDao.save(source);
            accountDao.save(dest);
        } else {
            throw new NotEnoughMoneyException();
        }
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
