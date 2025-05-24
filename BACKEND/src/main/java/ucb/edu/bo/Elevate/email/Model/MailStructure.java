package ucb.edu.bo.Elevate.email.Model;

public class MailStructure {
    private String subject;
    private String message;
    
    // Constructor vacío
    public MailStructure() {
    }
    
    // Constructor completo
    public MailStructure(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
    
    // Getters y setters
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

