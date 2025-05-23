package ucb.edu.bo.Elevate.DTO;

import java.time.LocalDateTime;

public class LogsSeguridadDTO {
    private Long logId;
    private Long userId;
    private String action;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    // Constructor
    public LogsSeguridadDTO(Long logId, Long userId, String action, String details, String ipAddress, LocalDateTime timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}