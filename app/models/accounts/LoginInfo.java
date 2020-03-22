package models.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInfo {
    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    private String username;
    private String password;

    public LoginInfo(@JsonProperty(value = "username", required = true)  String username,
                     @JsonProperty(value = "password", required = true) String password) {
        this.username = username;
        this.password = password;
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
}
