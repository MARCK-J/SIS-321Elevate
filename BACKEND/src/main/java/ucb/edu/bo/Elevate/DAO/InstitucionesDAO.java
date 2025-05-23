package ucb.edu.bo.Elevate.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucb.edu.bo.Elevate.Entity.Instituciones;

@Repository
public interface InstitucionesDAO extends JpaRepository<Instituciones, Long> {
    Instituciones findByDominioInstitucional(String dominioInstitucional);
}