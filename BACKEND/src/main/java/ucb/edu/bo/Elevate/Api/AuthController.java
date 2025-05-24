package ucb.edu.bo.Elevate.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import ucb.edu.bo.Elevate.BL.CaptchaService;
import ucb.edu.bo.Elevate.BL.UserBL;
import ucb.edu.bo.Elevate.DTO.RegisterRequestDTO;
import ucb.edu.bo.Elevate.DTO.LoginRequestDTO;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserBL userBL;
    
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO request) {
        // Verificar captcha antes de proceder con el registro
        if (!captchaService.validateCaptcha(request.getCaptchaToken())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Captcha inválido");
            response.put("success", false);
            
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        
        // Continuar con el registro normal
        return userBL.registerUser(request);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO request) {
        // Verificar captcha antes de proceder con el login
        if (!captchaService.validateCaptcha(request.getCaptchaToken())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Captcha inválido");
            response.put("success", false);
            
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        
        // Continuar con el login normal
        return userBL.loginUser(request);
    }
}