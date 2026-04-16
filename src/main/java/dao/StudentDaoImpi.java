package dao;

import org.springframework.stereotype.Repository;

@Repository
public class StudentDaoImpi implements StudentDao{

    @Override
    public void save(){
        System.out.println("保存学生信息");
    }

    @Override
    public void update(){
        System.out.println("更新学生信息");
    }
}
