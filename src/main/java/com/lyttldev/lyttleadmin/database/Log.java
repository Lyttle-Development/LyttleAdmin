package com.lyttldev.lyttleadmin.database;

import java.sql.Timestamp;

public class Log {
    private int id;
    private String uuid;
    private String username;
    private Timestamp dateCreated;
    private boolean enabled;
    private String message;

    // Constructor
    public Log(int id, String uuid, String username, Timestamp dateCreated, boolean enabled, String message) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.dateCreated = dateCreated;
        this.enabled = enabled;
        this.message = message;
    }

    // Getters and setters (optional, for access)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
