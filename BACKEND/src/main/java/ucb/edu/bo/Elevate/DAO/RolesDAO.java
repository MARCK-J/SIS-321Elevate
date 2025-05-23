package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucb.edu.bo.Elevate.Entity.Roles;

@Repository
public interface RolesDAO extends JpaRepository<Roles, Long> {
    Roles findByName(String name);
}
