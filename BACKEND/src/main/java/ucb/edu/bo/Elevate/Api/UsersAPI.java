package ucb.edu.bo.Elevate.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ucb.edu.bo.Elevate.BL.UsersBL;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.VerificationToken;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.Exception.UserException;
import ucb.edu.bo.Elevate.DTO.LoginRequestDTO;
import ucb.edu.bo.Elevate.DTO.PasswordChangeRequestDTO;
import ucb.edu.bo.Elevate.DTO.PasswordRecoveryRequestDTO;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.VerificationTokenDAO;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/user")
public class UsersAPI {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UsersAPI.class);
    private UsersBL userBl;
    private UsersDAO usersDao;
    private VerificationTokenDAO verificationTokenDao;

    @Autowired
    public UsersAPI(UsersBL userBl, UsersDAO usersDao, VerificationTokenDAO verificationTokenDao) {
        this.userBl = userBl;
        this.usersDao = usersDao;
        this.verificationTokenDao = verificationTokenDao;
    }

    // Endpoint para el registro (Sign-up)
    @PostMapping("/signup")
    public ResponseDTO signUp(@RequestBody Users user, @RequestParam("roleId") Long roleId) {
        try {
            Users createdUser = userBl.signUp(user, roleId);
            LOGGER.info("Usuario registrado correctamente");
            return new ResponseDTO(createdUser);
        } catch (UserException e) {
            LOGGER.error("Error en el registro del usuario", e);
            return new ResponseDTO("USER-1000", e.getMessage());
        }
    }

    // Endpoint para el inicio de sesión (Log-in)
    @PostMapping("/login")
    public ResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Users user = userBl.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            LOGGER.info("Inicio de sesión exitoso");
            return new ResponseDTO(user);
        } catch (UserException e) {
            LOGGER.error("Error en el inicio de sesión", e);
            return new ResponseDTO("USER-1001", e.getMessage());
        }
    }

    // Endpoint para obtener todos los usuarios
    @GetMapping("/all")
    public ResponseDTO getAllUsers() {
        try {
            return new ResponseDTO(userBl.getAllUsers());
        } catch (Exception e) {
            LOGGER.error("Error al obtener usuarios", e);
            return new ResponseDTO("USER-1002", "Error al obtener la lista de usuarios");
        }
    }

    // Endpoint para obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseDTO getUserById(@PathVariable("id") Long id) {
        try {
            return new ResponseDTO(userBl.getUserById(id));
        } catch (UserException e) {
            LOGGER.error("Error al obtener usuario", e);
            return new ResponseDTO("USER-1003", e.getMessage());
        }
    }

    // Endpoint para actualizar un usuario
    @PutMapping("/{id}")
    public ResponseDTO updateUser(@PathVariable("id") Long id, @RequestBody Users userDetails) {
        try {
            return new ResponseDTO(userBl.updateUser(id, userDetails));
        } catch (UserException e) {
            LOGGER.error("Error al actualizar usuario", e);
            return new ResponseDTO("USER-1004", e.getMessage());
        }
    }

    // Endpoint para eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseDTO deleteUser(@PathVariable("id") Long id) {
        try {
            userBl.deleteUser(id);
            LOGGER.info("Usuario eliminado correctamente");
            return new ResponseDTO("Usuario eliminado correctamente");
        } catch (UserException e) {
            LOGGER.error("Error al eliminar usuario", e);
            return new ResponseDTO("USER-1005", e.getMessage());
        }
    }

    // Endpoint para obtener usuarios por rol
    @GetMapping("/role/{roleId}")
    public ResponseDTO getUsersByRole(@PathVariable("roleId") Long roleId) {
        try {
            return new ResponseDTO(userBl.getUsersByRole(roleId));
        } catch (UserException e) {
            LOGGER.error("Error al obtener usuarios por rol", e);
            return new ResponseDTO("USER-1006", e.getMessage());
        }
    }

    @PostMapping("/recover-password")
    public ResponseDTO recoverPassword(@RequestBody PasswordRecoveryRequestDTO passwordRecoveryRequestDTO) {
        try {
            return userBl.recoverPassword(passwordRecoveryRequestDTO.getEmail());
        } catch (UserException e) {
            LOGGER.error("Error in password recovery", e);
            return new ResponseDTO("USER-1008", e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseDTO changePassword(@RequestBody PasswordChangeRequestDTO changePasswordRequestDTO) {
        try {
            return userBl.changePassword(changePasswordRequestDTO.getEmail(), changePasswordRequestDTO.getNewPassword());
        } catch (UserException e) {
            LOGGER.error("Error changing password", e);
            return new ResponseDTO("USER-1007", e.getMessage());
        }
    }

    @GetMapping("/email-exists")
    public ResponseDTO emailExists(@RequestParam("email") String email) {
        try {
            boolean exists = userBl.existsByEmail(email);
            return new ResponseDTO(exists);
        } catch (Exception e) {
            LOGGER.error("Error al verificar existencia de correo", e);
            return new ResponseDTO("USER-1010", e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseDTO verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenDao.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            return new ResponseDTO("TOKEN-INVALIDO", "El token es inválido o ha expirado");
        }
        Users user = usersDao.findById(verificationToken.getUserId()).orElse(null);
        if (user == null) {
            return new ResponseDTO("USUARIO-NO-ENCONTRADO", "Usuario no encontrado");
        }
        user.setActivation(true);
        usersDao.save(user);
        verificationTokenDao.deleteByToken(token);
        return new ResponseDTO("Cuenta verificada correctamente");
    }

    @PostMapping("/admin/create")
    public ResponseDTO createAdminUser(
            @RequestHeader(value = "adminUserId", required = false) Long adminUserId,
            @RequestBody Users newAdminUser,
            @RequestParam("roleId") Long roleId
    ) {
        try {
            // Permitir crear el primer admin si no existe ninguno
            if (userBl.existsAnyAdmin()) {
                // Verifica que el usuario que hace la solicitud es un administrador válido
                if (adminUserId == null) {
                    return new ResponseDTO("USER-403", "Debes estar autenticado como administrador para crear otros administradores.");
                }
                Users admin = userBl.getUserById(adminUserId);
                String adminRole = admin.getRole().getName();
                if (!"AdminPagina".equalsIgnoreCase(adminRole) && !"AdminUsuarios".equalsIgnoreCase(adminRole)) {
                    return new ResponseDTO("USER-403", "No tienes permisos para crear administradores.");
                }
            }
            // Solo permite crear usuarios con rol de administrador
            Roles newRole = userBl.getRoleById(roleId);
            if (newRole == null || (!"AdminPagina".equalsIgnoreCase(newRole.getName()) && !"AdminUsuarios".equalsIgnoreCase(newRole.getName()))) {
                return new ResponseDTO("USER-400", "Solo puedes crear usuarios administradores.");
            }
            Users created = userBl.createAdminUser(newAdminUser, roleId);
            return new ResponseDTO(created);
        } catch (UserException e) {
            return new ResponseDTO("USER-ERROR", e.getMessage());
        }
    }
}