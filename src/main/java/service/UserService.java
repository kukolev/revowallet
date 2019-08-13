package service;

import dao.UserDao;
import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static util.MapperUtils.str;

public class UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class.getCanonicalName());
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User persist(User user) {
        LOGGER.info("Start persist: user = {}", str(user));
        return userDao.persist(user);
    }
}
