package ucb.edu.bo.Elevate.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucb.edu.bo.Elevate.DAO.RolesDAO;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;

@Service
public class RolesBL {
    private final RolesDAO rolesDao;

    @Autowired
    public RolesBL(RolesDAO rolesDao) {
        this.rolesDao = rolesDao;
    }

    public ResponseDTO getAllRoles() {
        List<Roles> roles = rolesDao.findAll();
        return new ResponseDTO(roles);
    }

    public ResponseDTO getRoleById(Long id) {
        Roles role = rolesDao.findById(id).orElse(null);
        if (role == null) {
            return new ResponseDTO("ROLE-404", "Rol no encontrado");
        }
        return new ResponseDTO(role);
    }

    public ResponseDTO createRole(Roles role) {
        Roles created = rolesDao.save(role);
        return new ResponseDTO(created);
    }

    public ResponseDTO updateRole(Long id, Roles roleDetails) {
        Roles role = rolesDao.findById(id).orElse(null);
        if (role != null) {
            role.setName(roleDetails.getName());
            role.setDescription(roleDetails.getDescription());
            role.setCreationDate(roleDetails.getCreationDate());
            Roles updated = rolesDao.save(role);
            return new ResponseDTO(updated);
        }
        return new ResponseDTO("ROLE-404", "Rol no encontrado");
    }

    public ResponseDTO deleteRole(Long id) {
        if (!rolesDao.existsById(id)) {
            return new ResponseDTO("ROLE-404", "Rol no encontrado");
        }
        rolesDao.deleteById(id);
        return new ResponseDTO("Rol eliminado correctamente");
    }
}