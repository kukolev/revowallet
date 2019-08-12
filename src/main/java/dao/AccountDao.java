package dao;

import domain.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AccountDao extends AbstractDao<Account> {

    // todo: static
    private final String INSERT_ACCOUNT = "INSERT INTO Accounts(account_number, money, user_id) VALUES(?, ?, ?)";
    private final String UPDATE_ACCOUNT = "UPDATE Accounts SET account_number = ?, money = ?, user_id = ? WHERE account_id = ?";
    private final String SELECT_ACCOUNT_BY_ID = "SELECT account_id, account_number, money, user_id FROM Accounts WHERE account_id = ?";

    public AccountDao(Connection conn) {
        super(conn);
    }

    @Override
    public Account find(long id) {
        try {
            PreparedStatement statement = getConn().prepareStatement(SELECT_ACCOUNT_BY_ID, RETURN_GENERATED_KEYS);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Account account = new Account();
                account.setId(resultSet.getLong("account_id"));
                account.setAccountNumber(resultSet.getString("account_number"));
                account.setMoney(resultSet.getBigDecimal("money"));
                account.setUserId(resultSet.getLong("user_id"));
                getConn().commit();
                return account;
            }
            getConn().commit();
            return null;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Account obj) {
        try {
            PreparedStatement statement = getConn().prepareStatement(UPDATE_ACCOUNT);
            statement.setString(1, obj.getAccountNumber());
            statement.setBigDecimal(2, obj.getMoney());
            statement.setLong(3, obj.getUserId());
            statement.setLong(4, obj.getId());
            statement.executeUpdate();
            getConn().commit();
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account persist(Account obj) {
        try {
            PreparedStatement statement = getConn().prepareStatement(INSERT_ACCOUNT, RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getAccountNumber());
            statement.setBigDecimal(2, obj.getMoney());
            statement.setLong(3, obj.getUserId());

            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            long accountId = resultSet.getLong(1);
            obj.setId(accountId);
            getConn().commit();
            return obj;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }
}
