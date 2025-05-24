package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ucb.edu.bo.Elevate.DAO.UserDAO;
import ucb.edu.bo.Elevate.DTO.LoginRequestDTO;
import ucb.edu.bo.Elevate.DTO.RegisterRequestDTO;
import ucb.edu.bo.Elevate.Entity.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserBL {
    
    @Autowired
    private UserDAO userDAO;
    
    /**
     * Método para registrar un nuevo usuario
     * @param request Datos del registro
     * @return ResponseEntity con resultado del registro
     */
    public ResponseEntity<?> registerUser(RegisterRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar si el email ya está registrado
            if (userDAO.findByEmail(request.getEmail()) != null) {
                response.put("success", false);
                response.put("message", "El email ya está registrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Crear y guardar nuevo usuario
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // Idealmente encriptar la contraseña
            user.setRole(request.getRole());
            user.setStatus(1); // Activo
            
            userDAO.save(user);
            
            response.put("success", true);
            response.put("message", "Usuario registrado correctamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Método para autenticar un usuario
     * @param request Credenciales de login
     * @return ResponseEntity con resultado y token de ser exitoso
     */
    public ResponseEntity<?> loginUser(LoginRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar usuario por email
            User user = userDAO.findByEmail(request.getEmail());
            
            // Verificar si existe el usuario y la contraseña es correcta
            if (user == null || !user.getPassword().equals(request.getPassword())) { // Idealmente comparar contraseñas encriptadas
                response.put("success", false);
                response.put("message", "Credenciales incorrectas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Verificar que el usuario esté activo
            if (user.getStatus() != 1) {
                response.put("success", false);
                response.put("message", "Usuario inactivo");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Generar respuesta exitosa
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));
            // Aquí normalmente generarías un JWT token
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}