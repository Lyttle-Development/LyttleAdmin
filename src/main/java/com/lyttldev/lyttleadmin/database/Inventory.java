package com.lyttldev.lyttleadmin.database;

import java.sql.Timestamp;

public class Inventory {
    private int id;
    private String uuid;
    private String username;
    private String location;
    private Boolean enabled;
    private Timestamp dateCreated;
    private String inventoryContents;

    // Constructor
    public Inventory(int id, String uuid, String username, String location, Boolean enabled, Timestamp dateCreated, String inventoryContents) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.location = location;
        this.enabled = enabled;
        this.dateCreated = dateCreated;
        this.inventoryContents = inventoryContents;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getInventoryContents() {
        return inventoryContents;
    }

    public void setInventoryContents(String inventoryContents) {
        this.inventoryContents = inventoryContents;
    }
}
