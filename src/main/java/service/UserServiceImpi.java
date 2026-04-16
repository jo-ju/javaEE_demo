package service;

import dao.UserDao;

public class UserServiceImpi implements UserService{
    private UserDao userDao;
    public UserServiceImpi(UserDao userDao) {
        super();
        this.userDao = userDao;
    }
    @Override
    public void registerUser() {
        System.out.println("开始用户注册流程...");
        userDao.saveUser();
    }
}
