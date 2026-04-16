package mybatis.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.student;
import java.io.InputStream;

public class stuTest {
    public static void main(String[] args) {
        try {
            InputStream resource = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
            SqlSession ss = sqlSessionFactory.openSession();

            student student = ss.selectOne("get_stu_by_id",2);
            System.out.println(student);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
