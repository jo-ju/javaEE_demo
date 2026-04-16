package mybatis.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class TestDI {
    private static ApplicationContext appcon;
    public static void main(String[] args) {
        appcon = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserService us = (UserService)appcon.getBean("userService");
        us.registerUser();

//        String msg = (String) appcon.getBean("staticFactoryInstance");
//        System.out.println(msg);
    }
}
