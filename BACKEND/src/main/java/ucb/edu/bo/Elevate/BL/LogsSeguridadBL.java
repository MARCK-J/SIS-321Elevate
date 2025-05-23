package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.LogsSeguridadDAO;
import ucb.edu.bo.Elevate.DAO.UsersDAO;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.LogsSeguridad;
import ucb.edu.bo.Elevate.Entity.Users;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.LogsSeguridadDTO;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogsSeguridadBL {

    private final LogsSeguridadDAO logsSeguridadDao;
    private final UsersDAO usersDao;
    private final RolesDAO rolesDao;

    @Autowired
    public LogsSeguridadBL(LogsSeguridadDAO logsSeguridadDao, UsersDAO usersDao, RolesDAO rolesDao) {
        this.logsSeguridadDao = logsSeguridadDao;
        this.usersDao = usersDao;
        this.rolesDao = rolesDao;
    }

    private LogsSeguridadDTO toDTO(LogsSeguridad log) {
        return new LogsSeguridadDTO(
            log.getLogId(),
            log.getUserId(),
            log.getAction(),
            log.getDetails(),
            log.getIpAddress(),
            log.getTimestamp()
        );
    }

    public ResponseDTO getAllLogs() {
        List<LogsSeguridadDTO> logs = logsSeguridadDao.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return new ResponseDTO(logs);
    }

    public ResponseDTO getLogsByUserId(Long userId) {
        // Validar que el usuario existe y es válido
        if (!validateUser(userId)) {
            return new ResponseDTO("LOG-USER-404", "El usuario no existe");
        }
        List<LogsSeguridadDTO> logs = logsSeguridadDao.findByUserId(userId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return new ResponseDTO(logs);
    }

    public ResponseDTO getLogsByAction(String action) {
        List<LogsSeguridadDTO> logs = logsSeguridadDao.findByAction(action)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return new ResponseDTO(logs);
    }

    public ResponseDTO createLog(LogsSeguridad log) {
        // Validar que el usuario existe antes de crear el log
        if (!validateUser(log.getUserId())) {
            return new ResponseDTO("LOG-USER-404", "El usuario no existe");
        }
        LogsSeguridad created = logsSeguridadDao.save(log);
        return new ResponseDTO(toDTO(created));
    }

    public ResponseDTO deleteLogById(Long id) {
        logsSeguridadDao.deleteById(id);
        return new ResponseDTO("Log eliminado correctamente");
    }

    // Validación de que el usuario existe
    private boolean validateUser(Long userId) {
        if (userId == null) return false;
        Users user = usersDao.findById(userId).orElse(null);
        return user != null;
    }
}