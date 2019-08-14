package dao;

import domain.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserDao extends AbstractDao<User> {

    private static final String INSERT_USER = "INSERT INTO USERS(name, phone) VALUES(?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT user_id, name, phone FROM Users WHERE user_id = ?";
    private static final String SELECT_USER_BY_PHONE = "SELECT user_id, name, phone FROM Users WHERE phone = ?";
    private static final String SELECT_ACCOUNTS_BY_USER_ID = "SELECT account_id FROM Accounts WHERE user_id = ?";

    public UserDao(BasicDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public User find(long id) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(SELECT_USER_BY_ID, RETURN_GENERATED_KEYS);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setPhone(resultSet.getString("phone"));
                user.setName(resultSet.getString("name"));
                user.setAccounts(selectAccountIds(user.getId()));
                conn.commit();
                conn.close();
                return user;
            }
            conn.commit();
            conn.close();
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

    public User findUserByPhone(String phone) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(SELECT_USER_BY_PHONE, RETURN_GENERATED_KEYS);
            statement.setString(1, phone);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setPhone(resultSet.getString("phone"));
                user.setName(resultSet.getString("name"));
                user.setAccounts(selectAccountIds(user.getId()));
                conn.commit();
                conn.close();
                return user;
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

    @Override
    public User persist(User obj) {
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(INSERT_USER, RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getName());
            statement.setString(2, obj.getPhone());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            long userId = resultSet.getLong(1);
            obj.setId(userId);
            conn.commit();
            conn.close();
            return obj;
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

    private List<Long> selectAccountIds(Long userId) {
        Connection conn = getConn();
        try {
            List<Long> result = new ArrayList<>();
            PreparedStatement statement = conn.prepareStatement(SELECT_ACCOUNTS_BY_USER_ID, RETURN_GENERATED_KEYS);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("account_id"));
            }
            conn.commit();
            return result;
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
