package dao;

import domain.Account;
import org.apache.commons.dbcp2.BasicDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AccountDao extends AbstractDao<Account> {

    private static final String INSERT_ACCOUNT = "INSERT INTO Accounts(account_number, money, user_id) VALUES(?, ?, ?)";
    private static final String UPDATE_ACCOUNT = "UPDATE Accounts SET account_number = ?, money = ?, user_id = ? WHERE account_id = ?";
    private static final String SELECT_ACCOUNT_BY_ID = "SELECT account_id, account_number, money, user_id FROM Accounts WHERE account_id = ?";
    private static final String SELECT_ACCOUNT_BY_NUMBER = "SELECT account_id, account_number, money, user_id FROM Accounts WHERE account_number = ?";
    private static final String UPDATE_MONEY =
            "UPDATE Accounts SET money = money - ? WHERE account_id = ?;" +
                    "UPDATE Accounts SET money = money + ? WHERE account_id = ?";

    public AccountDao(BasicDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Account find(long id) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(SELECT_ACCOUNT_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Account account = new Account();
                account.setId(resultSet.getLong("account_id"));
                account.setAccountNumber(resultSet.getString("account_number"));
                account.setMoney(resultSet.getBigDecimal("money"));
                account.setUserId(resultSet.getLong("user_id"));
                conn.commit();
                conn.close();
                return account;
            }
            conn.commit();
            return null;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public Account findByNumber(String number) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(SELECT_ACCOUNT_BY_NUMBER);
            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Account account = new Account();
                account.setId(resultSet.getLong("account_id"));
                account.setAccountNumber(resultSet.getString("account_number"));
                account.setMoney(resultSet.getBigDecimal("money"));
                account.setUserId(resultSet.getLong("user_id"));
                conn.commit();
                conn.close();
                return account;
            }
            conn.commit();
            return null;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }


    public void save(Account obj) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(UPDATE_ACCOUNT);
            statement.setString(1, obj.getAccountNumber());
            statement.setBigDecimal(2, obj.getMoney());
            statement.setLong(3, obj.getUserId());
            statement.setLong(4, obj.getId());
            statement.executeUpdate();
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public Account persist(Account obj) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(INSERT_ACCOUNT, RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getAccountNumber());
            statement.setBigDecimal(2, obj.getMoney());
            statement.setLong(3, obj.getUserId());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            long accountId = resultSet.getLong(1);
            obj.setId(accountId);
            conn.commit();
            return obj;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void transferById(long idSource, long idDest, BigDecimal money) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(UPDATE_MONEY);
            statement.setBigDecimal(1, money);
            statement.setLong(2, idSource);
            statement.setBigDecimal(3, money);
            statement.setLong(4, idDest);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }
}
