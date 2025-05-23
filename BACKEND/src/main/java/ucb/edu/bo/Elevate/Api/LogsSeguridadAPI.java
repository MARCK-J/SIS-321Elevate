package ucb.edu.bo.Elevate.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucb.edu.bo.Elevate.BL.LogsSeguridadBL;
import ucb.edu.bo.Elevate.Entity.LogsSeguridad;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/logs-seguridad")
public class LogsSeguridadAPI {

    private final LogsSeguridadBL logsSeguridadBl;

    @Autowired
    public LogsSeguridadAPI(LogsSeguridadBL logsSeguridadBl) {
        this.logsSeguridadBl = logsSeguridadBl;
    }

    @GetMapping("/all")
    public ResponseDTO getAllLogs() {
        return logsSeguridadBl.getAllLogs();
    }

    @GetMapping("/user/{userId}")
    public ResponseDTO getLogsByUserId(@PathVariable("userId") Long userId) {
        return logsSeguridadBl.getLogsByUserId(userId);
    }

    @GetMapping("/action/{action}")
    public ResponseDTO getLogsByAction(@PathVariable("action") String action) {
        return logsSeguridadBl.getLogsByAction(action);
    }

    @PostMapping("/create")
    public ResponseDTO createLog(@RequestBody LogsSeguridad log) {
        return logsSeguridadBl.createLog(log);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO deleteLog(@PathVariable("id") Long id) {
        return logsSeguridadBl.deleteLogById(id);
    }
}