package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.ResultDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Results;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
public class ResultBL {

    private final ResultDAO resultDao;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public ResultBL(ResultDAO resultDao, UsersDAO usersDao, RolesDAO rolesDao) {
        this.resultDao = resultDao;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    public ResponseDTO getAllResults() {
        List<Results> results = resultDao.findAll();
        return new ResponseDTO(results);
    }

    public ResponseDTO getResultById(Integer id) {
        Results result = resultDao.findById(id).orElse(null);
        if (result == null) {
            return new ResponseDTO("RESULT-1000", "Result with id " + id + " does not exist");
        }
        return new ResponseDTO(result);
    }

    public ResponseDTO createResult(Results result) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(result.getStudentUserId())) {
            return new ResponseDTO("RESULT-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        Results createdResult = resultDao.save(result);
        return new ResponseDTO(createdResult);
    }

    public ResponseDTO updateResultById(Integer id, Results result) {
        Results currentResult = resultDao.findById(id).orElse(null);
        if (currentResult == null) {
            return new ResponseDTO("RESULT-1001", "Result does not exist");
        }
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(result.getStudentUserId())) {
            return new ResponseDTO("RESULT-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        currentResult.setScore(result.getScore());
        currentResult.setSubmissionDate(result.getSubmissionDate());
        currentResult.setQuizId(result.getQuizId());
        currentResult.setStudentUserId(result.getStudentUserId());
        return new ResponseDTO(resultDao.save(currentResult));
    }

    public ResponseDTO deleteResultById(Integer id) {
        Results result = resultDao.findById(id).orElse(null);
        if (result == null) {
            return new ResponseDTO("RESULT-1002", "Result does not exist");
        }
        resultDao.delete(result);
        return new ResponseDTO("Result deleted successfully");
    }

    public ResponseDTO getResultsByQuizId(Integer quizId) {
        List<Results> results = resultDao.findByQuizId(quizId);
        if (results.isEmpty()) {
            return new ResponseDTO("RESULT-1003", "No results found for quiz with id " + quizId);
        }
        return new ResponseDTO(results);
    }

    public ResponseDTO getResultsByStudentUserId(Integer studentUserId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(Long.valueOf(studentUserId))) {
            return new ResponseDTO("RESULT-USER-ROLE", "El usuario no es un Estudiante válido");
        }
        List<Results> results = resultDao.findByStudentUserId(studentUserId);
        if (results.isEmpty()) {
            return new ResponseDTO("RESULT-1004", "No results found for student with id " + studentUserId);
        }
        return new ResponseDTO(results);
    }

    public ResponseDTO getQualificationByQuizIdAndStudentUserId(Long quizId, Long studentUserId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(studentUserId)) {
            return new ResponseDTO("RESULT-USER-ROLE", "El usuario no es un Estudiante válido");
        }
        Optional<Results> result = resultDao.findByQuizIdAndStudentUserId(quizId, studentUserId);
        if (result.isEmpty()) {
            return new ResponseDTO("RESULT-1003", "No result found for quiz with id " + quizId + " and student with id " + studentUserId);
        }
        return new ResponseDTO(result.get().getScore());
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