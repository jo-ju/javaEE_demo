package mybatis.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.user;

import java.io.InputStream;
import java.util.List;

public class SqlTest {
    public static void main(String[] args) {
        try{
            InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
            SqlSession session = sqlSessionFactory.openSession();
            //添加一个用户
            user addUser = new user();
            addUser.setName("黎俊驹");
            session.insert("add_user", addUser);
            //查询所有用户
            List<user> allUserList1 = session.selectList("get_All_user");
            System.out.println("查询所有用户：");
            for(user u : allUserList1){
                System.out.println(u);
            }
            //删除一个用户
            session.delete("del_user", 104);
            //查询所有用户
            List<user> allUserList2 = session.selectList("get_All_user");
            System.out.println("查询所有用户：");
            for(user u : allUserList2){
                System.out.println(u);
            }

            session.commit();
            session.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
