package com.knowledgenest.Model;

public class UserModel {
    private String name, email, password, profile;

    // ✅ Default constructor (needed for Firebase)
    public UserModel() {
        this.profile = "";  // Default empty profile to avoid null errors
    }

    // ✅ Modified Constructor: No profile required
    public UserModel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = "";  // Default empty string for profile
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}
