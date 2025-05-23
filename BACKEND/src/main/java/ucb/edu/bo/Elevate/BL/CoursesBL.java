package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.CoursesDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Courses;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;
import ucb.edu.bo.Elevate.DTO.CustomResponseDTO;

import java.util.List;

@Service
public class CoursesBL {

    private final CoursesDAO coursesDao;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public CoursesBL(CoursesDAO coursesDao, UsersDAO usersDao, RolesDAO rolesDao) {
        this.coursesDao = coursesDao;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    public ResponseDTO getAllCourses() {
        List<Courses> courses = coursesDao.findAll();
        return new ResponseDTO(courses);
    }

    public ResponseDTO getCourseById(Long id) {
        Courses course = coursesDao.findById(id).orElse(null);
        if (course == null) {
            return new ResponseDTO("COURSE-1000", "Course with id " + id + " does not exist");
        }
        // Validar que el docente existe y es Docente
        if (!validateTeacher(course.getTeacherUserId())) {
            return new ResponseDTO("COURSE-USER-ROLE", "El usuario asignado no es un Docente válido");
        }
        return new ResponseDTO(course);
    }

    public ResponseDTO createCourse(Courses course) {
        // Validar que el usuario existe y es docente
        if (!validateTeacher(course.getTeacherUserId())) {
            return new ResponseDTO("COURSE-USER-ROLE", "El usuario asignado no es un Docente válido");
        }
        // Obtener el último course_id
        Long lastCourseId = coursesDao.findLastCourseId();
        long newCourseId = (lastCourseId != null) ? lastCourseId + 1 : 1;
        course.setCourseId(newCourseId);
        Courses createdCourse = coursesDao.save(course);
        return new ResponseDTO(createdCourse);
    }

    public ResponseDTO updateCourseById(Long id, Courses course) {
        Courses currentCourse = coursesDao.findById(id).orElse(null);
        if (currentCourse == null) {
            return new ResponseDTO("COURSE-1001", "Course does not exist");
        }
        // Validar que el docente existe y es Docente
        if (!validateTeacher(course.getTeacherUserId())) {
            return new ResponseDTO("COURSE-USER-ROLE", "El usuario asignado no es un Docente válido");
        }
        currentCourse.setTitle(course.getTitle());
        currentCourse.setImage(course.getImage());
        currentCourse.setDescription(course.getDescription());
        currentCourse.setDuration(course.getDuration());
        currentCourse.setRating(course.getRating());
        currentCourse.setAbilities(course.getAbilities());
        currentCourse.setAvailable(course.isAvailable());
        currentCourse.setTeacherUserId(course.getTeacherUserId());
        currentCourse.setCategoryCourseId(course.getCategoryCourseId());
        return new ResponseDTO(coursesDao.save(currentCourse));
    }

    public ResponseDTO deleteCourseById(Long id) {
        Courses course = coursesDao.findById(id).orElse(null);
        if (course == null) {
            return new ResponseDTO("COURSE-1002", "Course does not exist");
        }
        coursesDao.delete(course);
        return new ResponseDTO("Course deleted successfully");
    }

    public ResponseDTO getCoursesByDuration(String duration) {
        List<Courses> courses = coursesDao.findByDuration(duration);
        // Validar que los docentes de los cursos existen y son Docentes
        courses.removeIf(c -> !validateTeacher(c.getTeacherUserId()));
        return new ResponseDTO(courses);
    }

    public ResponseDTO getCoursesByRating(double rating) {
        List<Courses> courses = coursesDao.findByRating(rating);
        courses.removeIf(c -> !validateTeacher(c.getTeacherUserId()));
        return new ResponseDTO(courses);
    }

    public ResponseDTO getCoursesByUserId(Long userId) {
        // Validar que el usuario existe y es docente
        if (!validateTeacher(userId)) {
            return new ResponseDTO("COURSE-USER-ROLE", "El usuario no es un Docente válido");
        }
        List<Courses> courses = coursesDao.findByTeacherUserId(userId);
        if (courses.isEmpty()) {
            return new ResponseDTO("COURSE-1007", "No courses found for user with id " + userId);
        }
        return new ResponseDTO(courses);
    }

    public CustomResponseDTO getCoursesByTitle(String title, Pageable pageable) {
        Page<Courses> courses = coursesDao.findByTitleContainingIgnoreCase(title, pageable);
        // Filtrar cursos con docentes válidos
        List<Courses> filtered = courses.getContent();
        filtered.removeIf(c -> !validateTeacher(c.getTeacherUserId()));
        CustomResponseDTO.Result result = new CustomResponseDTO.Result(
            filtered,
            courses.getTotalElements(),
            courses.getTotalPages(),
            courses.getNumber()
        );
        return new CustomResponseDTO("200-OK", result, null);
    }

    public CustomResponseDTO getCoursesByRating(double rating, Pageable pageable) {
        Page<Courses> courses = coursesDao.findByRatingGreaterThanEqual(rating, pageable);
        List<Courses> filtered = courses.getContent();
        filtered.removeIf(c -> !validateTeacher(c.getTeacherUserId()));
        CustomResponseDTO.Result result = new CustomResponseDTO.Result(
            filtered,
            courses.getTotalElements(),
            courses.getTotalPages(),
            courses.getNumber()
        );
        return new CustomResponseDTO("200-OK", result, null);
    }

    public CustomResponseDTO getCoursesByDuration(String duration, Pageable pageable) {
        Page<Courses> courses = coursesDao.findByDurationContainingIgnoreCase(duration, pageable);
        List<Courses> filtered = courses.getContent();
        filtered.removeIf(c -> !validateTeacher(c.getTeacherUserId()));
        CustomResponseDTO.Result result = new CustomResponseDTO.Result(
            filtered,
            courses.getTotalElements(),
            courses.getTotalPages(),
            courses.getNumber()
        );
        return new CustomResponseDTO("200-OK", result, null);
    }

    // Validación de que el usuario existe y tiene rol Docente
    private boolean validateTeacher(Long userId) {
        if (userId == null) return false;
        Users user = usersDao.findById(userId).orElse(null);
        if (user == null) return false;
        Roles docenteRole = rolesDao.findByName("Docente");
        return docenteRole != null && user.getRole() != null &&
               docenteRole.getRoleId().equals(user.getRole().getRoleId());
    }
}