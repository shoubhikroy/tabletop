package models.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationInfo {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "RegistrationInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    private String email;

    public RegistrationInfo(@JsonProperty(value = "username", required = true)  String username,
                            @JsonProperty(value = "password", required = true) String password,
                            @JsonProperty(value = "email", required = true) String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
