package domain;

import java.math.BigDecimal;

public class Account extends AbstractDomain {

    private String accountNumber;
    private BigDecimal money;
    private Long userId;
    private boolean isActive;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void inc(BigDecimal money) {
        setMoney(this.money.add(money));
    }

    public void dec(BigDecimal money) {
        setMoney(this.money.subtract(money));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
