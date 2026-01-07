package com.smartqueue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String category;      // Hospital / Bank / Government
    private String service;
    private String department;
    private String subservice;

    private int tokenNumber;

    private String status;        // WAITING / SERVING / DONE

    private LocalDateTime createdAt = LocalDateTime.now();

    // NEW FIELD: processing time in minutes
    private int processingTimeMinutes = 5; // default 5 minutes per token

    public Token() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSubservice() { return subservice; }
    public void setSubservice(String subservice) { this.subservice = subservice; }

    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // NEW FIELD getters/setters
    public int getProcessingTimeMinutes() { return processingTimeMinutes; }
    public void setProcessingTimeMinutes(int processingTimeMinutes) { this.processingTimeMinutes = processingTimeMinutes; }
}
