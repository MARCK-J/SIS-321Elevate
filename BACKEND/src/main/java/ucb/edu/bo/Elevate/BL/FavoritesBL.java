package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.FavoritesDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Favorites;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
public class FavoritesBL {

    private final FavoritesDAO favoritesDao;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public FavoritesBL(FavoritesDAO favoritesDao, UsersDAO usersDao, RolesDAO rolesDao) {
        this.favoritesDao = favoritesDao;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    public ResponseDTO addFavorite(Long studentUserId, Long courseId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(studentUserId)) {
            return new ResponseDTO("FAVORITE-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        Favorites favorite = new Favorites();
        favorite.setStudentUserId(studentUserId);
        favorite.setCourseId(courseId);
        favoritesDao.save(favorite);
        return new ResponseDTO("Favorite added successfully");
    }

    public ResponseDTO getFavoritesByStudentUserId(Long studentUserId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(studentUserId)) {
            return new ResponseDTO("FAVORITE-USER-ROLE", "El usuario no es un Estudiante válido");
        }
        List<Favorites> favorites = favoritesDao.findByStudentUserId(studentUserId);
        return new ResponseDTO(favorites);
    }

    public ResponseDTO deleteFavorite(Long studentUserId, Long courseId) {
        // Validar que el usuario existe y es Estudiante
        if (!validateStudent(studentUserId)) {
            return new ResponseDTO("FAVORITE-USER-ROLE", "El usuario asignado no es un Estudiante válido");
        }
        Optional<Favorites> favorite = favoritesDao.findByStudentUserIdAndCourseId(studentUserId, courseId);
        if (favorite.isPresent()) {
            favoritesDao.delete(favorite.get());
            return new ResponseDTO("Favorite deleted successfully");
        } else {
            return new ResponseDTO("FAVORITE-NOT-FOUND", "Favorite not found");
        }
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