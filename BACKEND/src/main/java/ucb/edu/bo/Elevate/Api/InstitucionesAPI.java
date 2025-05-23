package ucb.edu.bo.Elevate.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucb.edu.bo.Elevate.BL.InstitucionesBL;
import ucb.edu.bo.Elevate.Entity.Instituciones;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/instituciones")
public class InstitucionesAPI {

    private final InstitucionesBL institucionesBl;

    @Autowired
    public InstitucionesAPI(InstitucionesBL institucionesBl) {
        this.institucionesBl = institucionesBl;
    }

    @GetMapping("/all")
    public ResponseDTO getAllInstituciones() {
        return institucionesBl.getAllInstituciones();
    }

    @GetMapping("/{id}")
    public ResponseDTO getInstitucionById(@PathVariable("id") Long id) {
        return institucionesBl.getInstitucionById(id);
    }

    @PostMapping("/create")
    public ResponseDTO createInstitucion(@RequestBody Instituciones institucion) {
        return institucionesBl.createInstitucion(institucion);
    }

    @PutMapping("/{id}")
    public ResponseDTO updateInstitucion(@PathVariable("id") Long id, @RequestBody Instituciones institucion) {
        return institucionesBl.updateInstitucionById(id, institucion);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO deleteInstitucion(@PathVariable("id") Long id) {
        return institucionesBl.deleteInstitucionById(id);
    }
}