package dao;

import domain.Account;

import java.sql.Connection;

public class AccountDao extends AbstractDao<Account> {

    public AccountDao(Connection conn) {
        super(conn);
    }

    public Account findByAccountNumber(String accountNumber) {
        return getData().values()
                .stream()
                .filter(account -> account.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }
}
