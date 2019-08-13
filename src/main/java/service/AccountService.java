package service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import domain.User;
import exception.AccountNotFoundException;
import exception.NotEnoughMoneyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.MapperUtils.str;

public class AccountService {

    private static final Logger LOGGER = LogManager.getLogger(AccountService.class.getCanonicalName());
    private final AccountDao accountDao;
    private final UserDao userDao;

    public AccountService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    public void transfer(Long idSource, Long idDest, BigDecimal money) {
        LOGGER.debug("Start transfer: idSource = {}, idDest = {}, money = {}");
        Account source = accountDao.find(idSource);
        Account dest = accountDao.find(idDest);

        validateAccounts(source, dest);
        validateEnoughMoney(source, money);

        accountDao.transfer(source, dest, money);
        LOGGER.debug("Finish transfer: idSource = {}, idDest = {}, money = {}");
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
        LOGGER.debug("Start find: id = {}", id);
        Account result = accountDao.find(id);
        LOGGER.debug("Finish fins: account = {}", result);
        return result;
    }

    public Account persist(Account account){
        LOGGER.debug("Start persist: account = {}", str(account));
        Account result = accountDao.persist(account);
        LOGGER.debug("Finish persist: account = {}", str(result));
        return result;
    }

    public List<Account> findAccountsByUserPhone(String phone) {
        LOGGER.debug("Start findAccountsByUserName: phone = {}", phone);
        User user = userDao.findUserByPhone(phone);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Account> result = user.getAccounts()
                .stream()
                .map(accountDao::find)
                .collect(Collectors.toList());
        LOGGER.debug("Finish findAccountsByUserName: result = {}", str(result));
        return result;
    }
}
