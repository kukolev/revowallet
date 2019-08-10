package dao;

import domain.User;

import java.sql.Connection;

public class UserDao extends AbstractDao<User> {

    public UserDao(Connection conn) {
        super(conn);
    }

    public User findUserByName(String name) {
        return getData().values()
                .stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public User findUserByPhone(String phone) {
        return getData().values()
                .stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }
}
