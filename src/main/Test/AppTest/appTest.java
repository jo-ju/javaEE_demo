package AppTest;

import config.SpringConifg;
import dao.StudentDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class appTest {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConifg.class);
        StudentDao dao1 = (StudentDao) context.getBean(StudentDao.class);
        System.out.println("调用保存的方法");
        dao1.save();
        System.out.println("调用更新的方法");
        dao1.update();
    }
}
