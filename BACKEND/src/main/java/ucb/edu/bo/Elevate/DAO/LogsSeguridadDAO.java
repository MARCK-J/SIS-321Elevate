package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucb.edu.bo.Elevate.Entity.LogsSeguridad;

import java.util.List;

@Repository
public interface LogsSeguridadDAO extends JpaRepository<LogsSeguridad, Long> {
    List<LogsSeguridad> findByUserId(Long userId);
    List<LogsSeguridad> findByAction(String action);
}