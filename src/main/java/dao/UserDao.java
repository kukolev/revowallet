package dao;

import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserDao extends AbstractDao<User> {

    private final String INSERT_USER = "INSERT INTO USERS(name, phone) VALUES(?, ?)";
    private final String SELECT_USER_BY_ID = "SELECT user_id, name, phone FROM Users WHERE user_id = ?";
    private final String SELECT_USER_BY_PHONE = "SELECT user_id, name, phone FROM Users WHERE phone = ?";
    private final String SELECT_ACCOUNTS_BY_USER_ID = "SELECT account_id FROM Accounts WHERE user_id = ?";


    public UserDao(Connection conn) {
        super(conn);
    }

    @Override
    public User find(long id) {
        try {
            PreparedStatement statement = getConn().prepareStatement(SELECT_USER_BY_ID, RETURN_GENERATED_KEYS);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setPhone(resultSet.getString("phone"));
                user.setName(resultSet.getString("name"));
                user.setAccounts(selectAccountIds(user.getId()));
                getConn().commit();
                return user;
            }
            getConn().commit();
            return null;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }

    public User findUserByPhone(String phone) {
        try {
            PreparedStatement statement = getConn().prepareStatement(SELECT_USER_BY_PHONE, RETURN_GENERATED_KEYS);
            statement.setString(1, phone);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setPhone(resultSet.getString("phone"));
                user.setName(resultSet.getString("name"));
                user.setAccounts(selectAccountIds(user.getId()));
                getConn().commit();
                return user;
            }
            getConn().commit();
            return null;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }

    @Override
    public User persist(User obj) {
        try {
            PreparedStatement statement = getConn().prepareStatement(INSERT_USER, RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getName());
            statement.setString(2, obj.getPhone());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            long userId = resultSet.getLong(1);
            obj.setId(userId);
            getConn().commit();
            return obj;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }

    // todo: implement
    @Override
    public void save(User obj) {

    }

    private List<Long> selectAccountIds(Long userId) {
        try {
            List<Long> result = new ArrayList<>();
            PreparedStatement statement = getConn().prepareStatement(SELECT_ACCOUNTS_BY_USER_ID, RETURN_GENERATED_KEYS);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("account_id"));
            }
            getConn().commit();
            return result;
        } catch (SQLException e) {
            // todo:
            throw new RuntimeException(e);
        }
    }
}
