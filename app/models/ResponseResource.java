package models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResource<T> {
    private String hash;
    private String endpoint;
    private String status;

    public ResponseResource(String hash, String endpoint, String status, T payload) {
        this.hash = hash;
        this.endpoint = endpoint;
        this.status = status;
        this.payload = payload;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    private List<String> errors;
    private T payload;

    public ResponseResource() {

    }

    @Override
    public String toString() {
        return "ResponseResource{" +
                "hash='" + hash + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", status='" + status + '\'' +
                ", errors=" + errors +
                ", payload=" + payload +
                '}';
    }

    public ResponseResource(String hash, String endpoint, String status, List<String> errors, T payload) {
        this.hash = hash;
        this.endpoint = endpoint;
        this.status = status;
        this.errors = errors;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
