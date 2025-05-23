package ucb.edu.bo.Elevate.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucb.edu.bo.Elevate.BL.RolesBL;
import ucb.edu.bo.Elevate.Entity.Roles;
import ucb.edu.bo.Elevate.DTO.ResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RolesAPI {
    private final RolesBL rolesBl;

    @Autowired
    public RolesAPI(RolesBL rolesBl) {
        this.rolesBl = rolesBl;
    }

    @GetMapping("/all")
    public ResponseDTO getAllRoles() {
        return new ResponseDTO(rolesBl.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseDTO getRoleById(@PathVariable Long id) {
        return new ResponseDTO(rolesBl.getRoleById(id));
    }

    @PostMapping("/create")
    public ResponseDTO createRole(@RequestBody Roles role) {
        return new ResponseDTO(rolesBl.createRole(role));
    }

    @PutMapping("/{id}")
    public ResponseDTO updateRole(@PathVariable Long id, @RequestBody Roles role) {
        return new ResponseDTO(rolesBl.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO deleteRole(@PathVariable Long id) {
        rolesBl.deleteRole(id);
        return new ResponseDTO("Rol eliminado correctamente");
    }
}