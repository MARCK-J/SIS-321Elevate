package ucb.edu.bo.Elevate.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "instituciones")
public class Instituciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institucion_id")
    private Long institucionId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "dominio_institucional", nullable = false, length = 100)
    private String dominioInstitucional;

    // Getters y Setters
    public Long getInstitucionId() {
        return institucionId;
    }

    public void setInstitucionId(Long institucionId) {
        this.institucionId = institucionId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDominioInstitucional() {
        return dominioInstitucional;
    }

    public void setDominioInstitucional(String dominioInstitucional) {
        this.dominioInstitucional = dominioInstitucional;
    }

    @Override
    public String toString() {
        return "Instituciones{" +
                "institucionId=" + institucionId +
                ", nombre='" + nombre + '\'' +
                ", dominioInstitucional='" + dominioInstitucional + '\'' +
                '}';
    }
}