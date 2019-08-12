package service;

import dao.UserDao;
import domain.User;

public class UserService extends AbstractService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User persist(User user) {
        return userDao.persist(user);
    }
}
