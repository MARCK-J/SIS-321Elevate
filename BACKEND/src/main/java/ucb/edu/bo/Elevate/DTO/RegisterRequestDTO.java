package ucb.edu.bo.Elevate.DTO;

public class RegisterRequestDTO extends CaptchaRequest {
    // Todos los campos necesarios para el registro
    private String name;
    private String email;
    private String password;
    private Integer role;
    
    // Getters y setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Integer getRole() {
        return role;
    }
    
    public void setRole(Integer role) {
        this.role = role;
    }
}