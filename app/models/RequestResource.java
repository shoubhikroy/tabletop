package models;

public class RequestResource<T> {
    private String hash;
    private String endpoint;

    public RequestResource(String hash, String endpoint, T payload) {
        this.hash = hash;
        this.endpoint = endpoint;
        this.payload = payload;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "RequestResource{" +
                "hash='" + hash + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", payload=" + payload +
                '}';
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

    private T payload;
}
