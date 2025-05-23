package ucb.edu.bo.Elevate.BL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.CertificationDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Certification;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.Date;

@Service
public class CertificationBL {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationBL.class);
    private final CertificationDAO certificationDao;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public CertificationBL(CertificationDAO certificationDao, UsersDAO usersDao, RolesDAO rolesDao) {
        this.certificationDao = certificationDao;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    public ResponseDTO generateCertification(Long studentUserId, Long courseId) {
        LOGGER.info("Generating certification for studentUserId: {} and courseId: {}", studentUserId, courseId);

        // Validar que el usuario existe y es Estudiante
        Users user = usersDao.findById(studentUserId).orElse(null);
        if (user == null) {
            return new ResponseDTO("CERT-USER-404", "El usuario no existe");
        }
        Roles studentRole = rolesDao.findByName("Estudiante");
        if (studentRole == null || user.getRole() == null || !studentRole.getRoleId().equals(user.getRole().getRoleId())) {
            return new ResponseDTO("CERT-USER-ROLE", "El usuario no tiene rol de Estudiante");
        }

        Certification certification = new Certification();
        certification.setStudentUserId(studentUserId);
        certification.setCourseId(courseId);
        certification.setIssueDate(new Date());
        Certification savedCertification = certificationDao.save(certification);
        LOGGER.info("Saved certification: {}", savedCertification);
        return new ResponseDTO(savedCertification);
    }
}