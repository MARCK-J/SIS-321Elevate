package ucb.edu.bo.Elevate.Api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucb.edu.bo.Elevate.BL.CertificationBL;
import ucb.edu.bo.Elevate.DTO.CertificationRequestDTO;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/certifications")
public class CertificationAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationAPI.class);
    private final CertificationBL certificationBl;

    @Autowired
    public CertificationAPI(CertificationBL certificationBl) {
        this.certificationBl = certificationBl;
    }

    @PostMapping("/generate")
    public ResponseDTO generateCertification(@RequestBody CertificationRequestDTO certificationRequest) {
        LOGGER.info("Received request to generate certification for studentUserId: {} and courseId: {}", certificationRequest.getStudentUserId(), certificationRequest.getCourseId());
        return certificationBl.generateCertification(certificationRequest.getStudentUserId(), certificationRequest.getCourseId());
    }
}