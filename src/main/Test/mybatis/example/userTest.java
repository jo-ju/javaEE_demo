package mybatis.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public class userTest {
    @Test
    public void findBy_id() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        try(SqlSession session = sqlSessionFactory.openSession()) {
            user user = session.selectOne("get_user_id", 1);
            session.close();
            System.out.println("用户ID:"+ user.getId()+"用户名:"+user.getName());

        }
    }
}
