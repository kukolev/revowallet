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
        account1.setAccountNumber("40817810099910000001");
        account1.setMoney(new BigDecimal("1000"));

        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("40817810099910000002");
        account2.setMoney(new BigDecimal("2000"));

        when(accountDao.find(1)).thenReturn(account1);
        when(accountDao.find(2)).thenReturn(account2);
        when(accountDao.findByNumber("40817810099910000001")).thenReturn(account1);
        when(accountDao.findByNumber("40817810099910000002")).thenReturn(account2);
    }

    @Test
    void testTransferById() {
        accountService.transferById(1L, 2L, new BigDecimal("100.10"));
        verify(accountDao, times(1))
                .transferById(1, 2, new BigDecimal("100.10"));
    }

    @Test
    void testTransferByNumber() {
        accountService.transferByNumber("40817810099910000001", "40817810099910000002", new BigDecimal("100.10"));
        verify(accountDao, times(1))
                .transferById(1, 2, new BigDecimal("100.10"));
    }

    @Test
    void testTransferValidateAccountExists() {
        try {
            accountService.transferById(100500L, 2L, new BigDecimal("100.10"));
        } catch (RuntimeException e) {
            assertEquals(AccountNotFoundException.class, e.getClass());
        }
    }

    @Test
    void testTransferValidateEnoughMoney() {
        try {
            accountService.transferById(1L, 2L, new BigDecimal("1000000000.0"));
        } catch (RuntimeException e) {
            assertEquals(NotEnoughMoneyException.class, e.getClass());
        }
    }
}
