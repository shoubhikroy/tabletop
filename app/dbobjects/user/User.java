package dbobjects.user;

import dbobjects.DBOject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Table(name = "users", indexes = {
        @Index(columnList = "id", name = "id"),
        @Index(columnList = "username", name = "username")})
@Entity
public class User extends DBOject {

    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "type", nullable = false)
    private int type;
    @Column(name = "roles", nullable = false)
    private int roles;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", roles=" + roles +
                '}';
    }

    public void setPassword(String password) {
        this.password = UserRepository.createPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoles() {
        return roles;
    }

    public void setRoles(int roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }
}
