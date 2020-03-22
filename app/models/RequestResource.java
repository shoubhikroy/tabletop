package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

//client sends hash (optional) and payload
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResource<T> {
    private String hash;
    private String endpoint;
    private String username;

    @Override
    public String toString() {
        return "RequestResource{" +
                "hash='" + hash + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", roles=" + roles +
                ", ipAddress='" + ipAddress + '\'' +
                ", permissions='" + permissions + '\'' +
                ", payload=" + payload +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Integer getRoles() {
        return roles;
    }

    public void setRoles(Integer roles) {
        this.roles = roles;
    }

    private Integer roles;
    private String ipAddress;
    private String permissions;
    private T payload;

    @JsonCreator
    public RequestResource(@JsonProperty("hash") String hash,
                           @JsonProperty("endpoint") String endpoint,
                           @JsonProperty(value = "payload") T payload) {
        this.hash = hash;
        this.endpoint = endpoint;
        this.payload = payload;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
