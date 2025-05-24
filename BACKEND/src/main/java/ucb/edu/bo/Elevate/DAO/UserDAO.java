package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ucb.edu.bo.Elevate.Entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    User findByEmail(String email);
}