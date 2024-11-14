package com.example.assignment;

public class TeamMembersData {
    private String name;
    private String email;

    public TeamMembersData() {
        // Default constructor required for Firebase
    }

    public TeamMembersData(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
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
}
