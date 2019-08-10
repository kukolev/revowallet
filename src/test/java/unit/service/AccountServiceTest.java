package unit.service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import service.AccountService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    private AccountService accountService;
    private AccountDao accountDao;
    private UserDao userDao;

    @BeforeEach
    public void init() {
        accountDao = mock(AccountDao.class);
        userDao = mock(UserDao.class);
        accountService = new AccountService(accountDao, userDao);
        MockitoAnnotations.initMocks(AccountServiceTest.class);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setAccountNumber("1");
        account1.setActive(true);
        account1.setMoney(new BigDecimal("1000"));

        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("2");
        account2.setActive(true);
        account2.setMoney(new BigDecimal("2000"));

        when(accountDao.find(1)).thenReturn(account1);
        when(accountDao.find(2)).thenReturn(account2);
    }

    @Test
    public void testTransfer() {
        accountService.transfer(1, 2, new BigDecimal("100.10"));
        Account accSource = accountDao.find(1);
        Account accDest = accountDao.find(2);
        assertEquals("899.90", accSource.getMoney().toString());
        assertEquals("2100.10", accDest.getMoney().toString());
    }
}
