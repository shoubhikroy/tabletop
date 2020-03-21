package models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResource<T> {
    private String hash;
    private String endpoint;
    private String status;
    private ErrorResource errors;
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

    public ResponseResource(String hash, String endpoint, String status, ErrorResource errors, T payload) {
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

    public ErrorResource getErrors() {
        return errors;
    }

    public void setErrors(ErrorResource errors) {
        this.errors = errors;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
