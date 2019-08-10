package dao;

import domain.User;

public class UserDao extends AbstractDao<User> {

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
