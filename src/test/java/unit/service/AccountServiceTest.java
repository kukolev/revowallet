package unit.service;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import exception.AccountNotFoundException;
import exception.NotEnoughMoneyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import service.AccountService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private AccountService accountService;
    private AccountDao accountDao;

    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        UserDao userDao = mock(UserDao.class);
        accountService = new AccountService(accountDao, userDao);
        MockitoAnnotations.initMocks(AccountServiceTest.class);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setAccountNumber("1");
        account1.setMoney(new BigDecimal("1000"));

        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("2");
        account2.setMoney(new BigDecimal("2000"));

        when(accountDao.find(1)).thenReturn(account1);
        when(accountDao.find(2)).thenReturn(account2);
    }

    @Test
    void testTransfer() {
        accountService.transfer(1L, 2L, new BigDecimal("100.10"));
        Account account1 = accountDao.find(1);
        Account account2 = accountDao.find(2);
        verify(accountDao, times(1)).transfer(account1, account2, new BigDecimal("100.10"));
    }

    @Test
    void testTransferValidateAccountExists() {
        try {
            accountService.transfer(100500L, 2L, new BigDecimal("100.10"));
        } catch (RuntimeException e) {
            assertEquals(AccountNotFoundException.class, e.getClass());
        }
    }

    @Test
    void testTransferValidateEnoughMoney() {
        try {
            accountService.transfer(1L, 2L, new BigDecimal("1000000000.0"));
        } catch (RuntimeException e) {
            assertEquals(NotEnoughMoneyException.class, e.getClass());
        }
    }
}
