package com.grpproject.store.UserManagement.Repository;




import com.grpproject.store.UserManagement.Model.User;
import java.util.List;

public interface UserRepository {

    void save(User user);

    User findById(int id);

    User findByEmail(String email);

    List<User> findAll();

    void update(User user);

    void delete(int id);
}

