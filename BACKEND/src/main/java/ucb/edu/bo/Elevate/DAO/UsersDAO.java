package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import ucb.edu.bo.Elevate.Entity.Users;

@Repository
public interface UsersDAO extends JpaRepository<Users, Long> {

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Users findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    Users findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE user_id = ?1", nativeQuery = true)
    List<Users> findUserByUserId(Long userId);

    @Query("SELECT u FROM Users u WHERE u.role.roleId = ?1")
    List<Users> findUsersByRoleId(Long roleId);

    @Query(value = "SELECT * FROM users WHERE verification = ?1", nativeQuery = true)
    List<Users> findUsersByVerification(boolean verification);

    @Query(value = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1", nativeQuery = true)
    Long findLastUserId();
    
    // Nuevo método para verificar si un correo electrónico existe
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE email = ?1", nativeQuery = true)
    boolean existsByEmail(String email);
}