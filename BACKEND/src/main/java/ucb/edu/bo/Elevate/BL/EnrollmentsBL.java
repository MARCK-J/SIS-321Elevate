package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.EnrollmentsDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Enrollments;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;

@Service
public class EnrollmentsBL {

    private final EnrollmentsDAO enrollmentsDao;
    private final CertificationBL certificationBl;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public EnrollmentsBL(EnrollmentsDAO enrollmentsDao, CertificationBL certificationBl, UsersDAO usersDao, RolesDAO rolesDao) {
        this.enrollmentsDao = enrollmentsDao;
        this.certificationBl = certificationBl;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    public ResponseDTO getAllEnrollments() {
        List<Enrollments> enrollments = enrollmentsDao.findAll();
        return new ResponseDTO(enrollments);
    }

    public ResponseDTO getEnrollmentById(Long id) {
        Enrollments enrollment = enrollmentsDao.findById(id).orElse(null);
        if (enrollment == null) {
            return new ResponseDTO("ENROLLMENT-1000", "Enrollment with id " + id + " does not exist");
        }
        return new ResponseDTO(enrollment);
    }

    public ResponseDTO createEnrollment(Enrollments enrollment) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(enrollment.getStudentUserId())) {
            return new ResponseDTO("ENROLLMENT-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        Long lastEnrollmentId = enrollmentsDao.findLastEnrollmentId();
        long newEnrollmentId = (lastEnrollmentId != null) ? lastEnrollmentId + 1 : 1;
        enrollment.setEnrollmentId(newEnrollmentId);
        Enrollments createdEnrollment = enrollmentsDao.save(enrollment);
        return new ResponseDTO(createdEnrollment);
    }

    public ResponseDTO updateEnrollmentById(Long id, Enrollments enrollment) {
        Enrollments currentEnrollment = enrollmentsDao.findById(id).orElse(null);
        if (currentEnrollment == null) {
            return new ResponseDTO("ENROLLMENT-1001", "Enrollment does not exist");
        }
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(enrollment.getStudentUserId())) {
            return new ResponseDTO("ENROLLMENT-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        currentEnrollment.setEnrollmentDate(enrollment.getEnrollmentDate());
        currentEnrollment.setStudentUserId(enrollment.getStudentUserId());
        currentEnrollment.setCoursesCourseId(enrollment.getCoursesCourseId());
        currentEnrollment.setCompleted(enrollment.isCompleted());
        return new ResponseDTO(enrollmentsDao.save(currentEnrollment));
    }

    public ResponseDTO deleteEnrollmentById(Long id) {
        Enrollments enrollment = enrollmentsDao.findById(id).orElse(null);
        if (enrollment == null) {
            return new ResponseDTO("ENROLLMENT-1002", "Enrollment does not exist");
        }
        enrollmentsDao.delete(enrollment);
        return new ResponseDTO("Enrollment deleted successfully");
    }

    public ResponseDTO getEnrollmentsByStudentId(Long studentUserId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(studentUserId)) {
            return new ResponseDTO("ENROLLMENT-USER-ROLE", "El usuario no es un Estudiante válido");
        }
        List<Enrollments> enrollments = enrollmentsDao.findByStudentUserId(studentUserId);
        if (enrollments.isEmpty()) {
            return new ResponseDTO("ENROLLMENT-1002", "No enrollments found for student with id " + studentUserId);
        }
        return new ResponseDTO(enrollments);
    }

    public void completeCourse(Long enrollmentId) {
        Enrollments enrollment = enrollmentsDao.findById(enrollmentId).orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setCompleted(true);
        enrollmentsDao.save(enrollment);

        // Generate certification
        certificationBl.generateCertification(enrollment.getStudentUserId(), enrollment.getCoursesCourseId());
    }

    // Validación de que el usuario existe y tiene rol Estudiante
    private boolean validateStudent(Long userId) {
        if (userId == null) return false;
        Users user = usersDao.findById(userId).orElse(null);
        if (user == null) return false;
        Roles studentRole = rolesDao.findByName("Estudiante");
        return studentRole != null && user.getRole() != null &&
               studentRole.getRoleId().equals(user.getRole().getRoleId());
    }
}