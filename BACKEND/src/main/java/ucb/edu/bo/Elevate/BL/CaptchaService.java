package ucb.edu.bo.Elevate.BL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @Value("${google.recaptcha.key.secret}")
    private String recaptchaSecret;
    
    @Value("${google.recaptcha.verify.url}")
    private String recaptchaVerifyUrl;
    
    @Value("${captcha.validation.enabled:true}")
    private boolean captchaEnabled;

    /**
     * Valida el token de captcha recibido del frontend
     * @param captchaToken Token captcha a validar
     * @return true si el captcha es válido, false en caso contrario
     */
    public boolean validateCaptcha(String captchaToken) {
        if (!captchaEnabled) {
            logger.warn("Captcha validation is disabled. Allowing request without validation.");
            return true;
        }
        
        if (captchaToken == null || captchaToken.isEmpty()) {
            logger.error("Captcha token is null or empty");
            return false;
        }

        try {
            Map<String, String> body = new HashMap<>();
            body.put("secret", recaptchaSecret);
            body.put("response", captchaToken);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject(recaptchaVerifyUrl, body, String.class);
            
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            boolean success = jsonObject.get("success").getAsBoolean();
            
            if (!success) {
                logger.error("Captcha validation failed: {}", response);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("Error validating captcha", e);
            return false;
        }
    }
}