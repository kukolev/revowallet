package service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import domain.User;
import exception.AccountNotFoundException;
import exception.NotEnoughMoneyException;

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

    public void transfer(Long idSource, Long idDest, BigDecimal money) {
        log(Level.INFO, "Start transfer");
        Account source = accountDao.find(idSource);
        Account dest = accountDao.find(idDest);

        validateAccounts(source, dest);
        validateEnoughMoney(source, money);

        accountDao.transfer(source, dest, money);
    }

    private void validateAccounts(Account source, Account dest) {
        if (source == null || dest == null) {
            throw new AccountNotFoundException();
        }
    }

    private void validateEnoughMoney(Account source, BigDecimal transferMoney) {
        BigDecimal newSourceMoney = source.getMoney().subtract(transferMoney);
        if (newSourceMoney.compareTo(BigDecimal.valueOf(0L)) < 0) {
            throw new NotEnoughMoneyException();
        }
    }

    public Account find(long id) {
        return accountDao.find(id);
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
