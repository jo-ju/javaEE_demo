package mybatis.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.user;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class userTest {
    @Test
    public void select_All_user() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        try(SqlSession session = sqlSessionFactory.openSession()) {

            List<user> allUserList = session.selectList("get_All_user");
            System.out.println("查询所有用户：");
            for(user u : allUserList){
                System.out.println(u);
            }
            session.commit();
            session.close();
        }
    }
//    @Test
    public void findBy_id() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        try(SqlSession session = sqlSessionFactory.openSession()) {
            user user = session.selectOne("get_user_by_id", 2);
            System.out.println("查询ID为2的用户信息");
            System.out.println("用户ID:"+ user.getId()+"用户名:"+user.getName());

            session.commit();
            session.close();
        }
    }
//    @Test
    public void Add_user() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        try(SqlSession session = sqlSessionFactory.openSession()) {
            user addUser = new user();
            addUser.setName("黎俊驹");
            session.insert("add_user", addUser);
            session.commit();
            session.close();
        }
    }
    @Test
    public void del_user() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try(SqlSession session = sqlSessionFactory.openSession()) {
            session.delete("del_user", 104);
            session.commit();
            session.close();
        }
    }
}
