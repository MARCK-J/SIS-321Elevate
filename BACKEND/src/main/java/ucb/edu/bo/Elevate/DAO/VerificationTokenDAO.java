package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import ucb.edu.bo.Elevate.Entity.VerificationToken;

public interface VerificationTokenDAO extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    void deleteByToken(String token);
}