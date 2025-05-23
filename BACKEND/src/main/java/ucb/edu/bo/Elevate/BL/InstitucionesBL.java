package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.InstitucionesDAO;
import ucb.edu.bo.Elevate.Entity.Instituciones;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;
import java.util.List;

@Service
public class InstitucionesBL {

    private final InstitucionesDAO institucionesDao;

    @Autowired
    public InstitucionesBL(InstitucionesDAO institucionesDao) {
        this.institucionesDao = institucionesDao;
    }

    public ResponseDTO getAllInstituciones() {
        List<Instituciones> instituciones = institucionesDao.findAll();
        return new ResponseDTO(instituciones);
    }

    public ResponseDTO getInstitucionById(Long id) {
        Instituciones institucion = institucionesDao.findById(id).orElse(null);
        if (institucion == null) {
            return new ResponseDTO("INSTITUCION-1000", "Institución con id " + id + " no existe");
        }
        return new ResponseDTO(institucion);
    }

    public ResponseDTO createInstitucion(Instituciones institucion) {
        Instituciones created = institucionesDao.save(institucion);
        return new ResponseDTO(created);
    }

    public ResponseDTO updateInstitucionById(Long id, Instituciones institucion) {
        Instituciones current = institucionesDao.findById(id).orElse(null);
        if (current == null) {
            return new ResponseDTO("INSTITUCION-1001", "Institución no existe");
        }
        current.setNombre(institucion.getNombre());
        current.setDominioInstitucional(institucion.getDominioInstitucional());
        return new ResponseDTO(institucionesDao.save(current));
    }

    public ResponseDTO deleteInstitucionById(Long id) {
        Instituciones institucion = institucionesDao.findById(id).orElse(null);
        if (institucion == null) {
            return new ResponseDTO("INSTITUCION-1002", "Institución no existe");
        }
        institucionesDao.delete(institucion);
        return new ResponseDTO("Institución eliminada correctamente");
    }
}