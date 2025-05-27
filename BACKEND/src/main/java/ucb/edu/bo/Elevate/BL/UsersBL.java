package ucb.edu.bo.Elevate.BL;

import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.DAO.LogsSeguridadDAO;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.Entity.VerificationToken;
import ucb.edu.bo.Elevate.Exception.UserException;
import ucb.edu.bo.Elevate.email.Model.MailStructure;
import ucb.edu.bo.Elevate.email.Service.MailService;
import ucb.edu.bo.Elevate.Entity.Instituciones;
import ucb.edu.bo.Elevate.DAO.InstitucionesDAO;
import ucb.edu.bo.Elevate.DAO.VerificationTokenDAO;
import ucb.edu.bo.Elevate.BL.LogsSeguridadBL;
import ucb.edu.bo.Elevate.Entity.LogsSeguridad;

@Service
public class UsersBL {

    private UsersDAO usersDao;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private MailService mailService;
    private InstitucionesDAO institucionesDao;
    private VerificationTokenDAO verificationTokenDao;
    @Autowired
    private RolesDAO rolesDao;
    @Autowired
    private LogsSeguridadBL logsSeguridadBL;
    @Autowired
    private LogsSeguridadDAO logsSeguridadDao;

    @Autowired
    public UsersBL(
        UsersDAO usersDao,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        MailService mailService,
        InstitucionesDAO institucionesDao,
        VerificationTokenDAO verificationTokenDao,
        RolesDAO rolesDao,
        LogsSeguridadBL logsSeguridadBL,
        LogsSeguridadDAO logsSeguridadDao
    ) {
        this.usersDao = usersDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailService = mailService;
        this.institucionesDao = institucionesDao;
        this.verificationTokenDao = verificationTokenDao;
        this.rolesDao = rolesDao;
        this.logsSeguridadBL = logsSeguridadBL;
        this.logsSeguridadDao = logsSeguridadDao;
    }

    // Método privado para registrar logs (guarda SIEMPRE el log)
    private void logAction(Long userId, String action, String details, String ipAddress) {
        LogsSeguridad log = new LogsSeguridad();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress != null ? ipAddress : "N/A");
        log.setTimestamp(LocalDateTime.now());
        logsSeguridadDao.save(log); // Guarda directamente el log
    }

    // Registro (Sign-up)
    public Users signUp(Users user, Long roleId) throws UserException {
        Users existingUser = usersDao.findByEmail(user.getEmail());
        if (existingUser != null) {
            logAction(null, "REGISTER_FAILED", "Intento de registro con email ya registrado: " + user.getEmail(), null);
            throw new UserException("El correo ya está registrado");
        }

        // Obtener el último user_id
        Long lastUserId = usersDao.findLastUserId();
        long newUserId = (lastUserId != null) ? lastUserId + 1 : 1;
        user.setUserId(newUserId);

        if (!isValidEmail(user.getEmail())) {
            logAction(null, "REGISTER_FAILED", "Formato de correo inválido: " + user.getEmail(), null);
            throw new UserException("El correo electrónico no tiene un formato válido.");
        }

        if (!isPasswordSecure(user.getPassword(), user)) {
            logAction(null, "REGISTER_FAILED", "Contraseña insegura para: " + user.getEmail(), null);
            throw new UserException("La contraseña no cumple con los requisitos de seguridad.");
        }

        Roles role = rolesDao.findById(roleId).orElseThrow(() -> new UserException("Rol no encontrado"));
        user.setRole(role);

        // Validación adicional para docentes
        if ("Docente".equalsIgnoreCase(role.getName())) {
            Instituciones institucion = institucionesDao.findById(user.getInstitucionId()).orElse(null);
            if (institucion == null) {
                logAction(null, "REGISTER_FAILED", "Institución no válida para docente: " + user.getEmail(), null);
                throw new UserException("Debe seleccionar una institución válida.");
            }
            String dominio = institucion.getDominioInstitucional();
            if (!user.getEmail().endsWith("@" + dominio)) {
                logAction(null, "REGISTER_FAILED", "Dominio de correo no coincide para docente: " + user.getEmail(), null);
                throw new UserException("El correo no corresponde al dominio institucional: " + dominio);
            }
        }

        if ("AdminPagina".equalsIgnoreCase(role.getName()) || "AdminUsuarios".equalsIgnoreCase(role.getName())) {
            logAction(null, "REGISTER_FAILED", "Intento de registro como administrador: " + user.getEmail(), null);
            throw new UserException("No puedes registrarte como administrador. Contacta al administrador del sistema.");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        Users savedUser = usersDao.save(user);

        user.setActivation(false);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(savedUser.getUserId());
        verificationToken.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 24 horas
        verificationTokenDao.save(verificationToken);

        mailService.sendMail(
            user.getEmail(),
            new MailStructure(
                "Verifica tu cuenta",
                "Haz clic en el enlace para activar tu cuenta: http://localhost:9999/api/v1/user/verify?token=" + token
            )
        );

        logAction(savedUser.getUserId(), "USER_REGISTERED", "Usuario registrado con rol " + savedUser.getRole().getName(), null);
        return savedUser;
    }

    // Inicio de sesión (Log-in) (solo email y contraseña)
    public Users login(String email, String password) throws UserException {
        Users user = usersDao.findByEmail(email);
        if (user == null) {
            logAction(null, "LOGIN_FAILED", "Intento de login fallido para: " + email, null);
            throw new UserException("Correo o contraseña incorrectos");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            logAction(user.getUserId(), "LOGIN_FAILED", "Contraseña incorrecta para: " + email, null);
            throw new UserException("Correo o contraseña incorrectos");
        }
        logAction(user.getUserId(), "LOGIN_SUCCESS", "Inicio de sesión exitoso", null);
        user.setPassword(null);
        return user;
    }

    // Obtener todos los usuarios
    public List<Users> getAllUsers() {
        return usersDao.findAll();
    }

    // Obtener un usuario por ID
    public Users getUserById(Long userId) throws UserException {
        return usersDao.findById(userId).orElseThrow(() -> new UserException("Usuario no encontrado"));
    }

    // Actualizar un usuario
    public Users updateUser(Long userId, Users userDetails) throws UserException {
        Users user = usersDao.findById(userId).orElseThrow(() -> new UserException("Usuario no encontrado"));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        }
        user.setVerification(userDetails.isVerification());
        user.setActivation(userDetails.isActivation());
        // Si se quiere actualizar el rol:
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        Users updated = usersDao.save(user);
        logAction(userId, "USER_UPDATED", "Usuario actualizado", null);
        return updated;
    }

    // Eliminar un usuario
    public void deleteUser(Long userId) throws UserException {
        Users user = usersDao.findById(userId).orElseThrow(() -> new UserException("Usuario no encontrado"));
        usersDao.delete(user);
        logAction(userId, "USER_DELETED", "Usuario eliminado", null);
    }

    // Obtener usuarios por rol
    public List<Users> getUsersByRole(Long roleId) throws UserException {
        List<Users> users = usersDao.findUsersByRoleId(roleId);
        if (users.isEmpty()) {
            throw new UserException("No se encontraron usuarios con este rol");
        }
        return users;
    }

    // Obtener usuarios por username
    public Users getUserByUsername(String username) throws UserException {
        Users user = usersDao.findByUsername(username);
        if (user == null) {
            throw new UserException("Usuario no encontrado");
        }
        return user;
    }

    public ResponseDTO recoverPassword(String email) throws UserException {
        Users user = usersDao.findByEmail(email);
        if (user == null) {
            logAction(null, "RECOVER_PASSWORD_FAILED", "Intento de recuperación para email inexistente: " + email, null);
            throw new UserException("No user found with the provided email");
        }
        String tempPassword = generateTemporaryPassword();
        user.setPassword(bCryptPasswordEncoder.encode(tempPassword));
        usersDao.save(user);
        MailStructure mailStructure = new MailStructure("Password Recovery", "Your temporary password is: " + tempPassword);
        mailService.sendMail(email, mailStructure);
        logAction(user.getUserId(), "RECOVER_PASSWORD", "Recuperación de contraseña solicitada", null);
        return new ResponseDTO("A temporary password has been sent to your email");
    }

    private String generateTemporaryPassword() {
        return "tempPassword123";
    }

    public ResponseDTO changePassword(String email, String newPassword) throws UserException {
        Users user = usersDao.findByEmail(email);
        if (user == null) {
            logAction(null, "CHANGE_PASSWORD_FAILED", "Intento de cambio de contraseña para email inexistente: " + email, null);
            throw new UserException("No user found with the provided email");
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        usersDao.save(user);
        logAction(user.getUserId(), "CHANGE_PASSWORD", "Contraseña cambiada", null);
        return new ResponseDTO("Password has been changed successfully");
    }

    public boolean existsByEmail(String email) {
        return usersDao.existsByEmail(email);
    }

    private boolean isPasswordSecure(String password, Users user) {
        if (password == null || password.length() < 12) {
            return false;
        }
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-\\[\\]{};':\"\\\\|,.<>/?].*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public Users createAdminUser(Users user, Long roleId) throws UserException {
        Roles role = rolesDao.findById(roleId).orElseThrow(() -> new UserException("Rol no encontrado"));
        if (!"AdminPagina".equalsIgnoreCase(role.getName()) && !"OSI".equalsIgnoreCase(role.getName())) {
            throw new UserException("Solo puedes crear usuarios administradores.");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new UserException("El correo electrónico no tiene un formato válido.");
        }
        if (!isPasswordSecure(user.getPassword(), user)) {
            throw new UserException("La contraseña no cumple con los requisitos de seguridad.");
        }
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setActivation(false);
        user.setRole(role);
        Users savedUser = usersDao.save(user);
        logAction(savedUser.getUserId(), "ADMIN_CREATED", "Administrador creado con rol " + savedUser.getRole().getName(), null);
        // Enviar correo de verificación si corresponde (igual que en signUp)
        return savedUser;
    }

    public Roles getRoleById(Long roleId) {
        return rolesDao.findById(roleId).orElse(null);
    }

    public boolean existsAnyAdmin() {
        List<Users> adminsPagina = usersDao.findUsersByRoleId(getRoleIdByName("AdminPagina"));
        List<Users> adminsUsuarios = usersDao.findUsersByRoleId(getRoleIdByName("OSI"));
        return (!adminsPagina.isEmpty() || !adminsUsuarios.isEmpty());
    }

    private Long getRoleIdByName(String roleName) {
        Roles role = rolesDao.findByName(roleName);
        return (role != null) ? role.getRoleId() : null;
    }
}