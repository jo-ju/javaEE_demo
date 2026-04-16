package dao;

import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao {
    public void save();
    public void update();
}
