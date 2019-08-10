package dao;

import domain.Account;

public class AccountDao extends AbstractDao<Account> {

    public Account findByAccountNumber(String accountNumber) {
        return getData().values()
                .stream()
                .filter(account -> account.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }
}
