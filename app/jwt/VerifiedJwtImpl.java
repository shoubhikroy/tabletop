package jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import play.libs.Json;

import java.util.Date;

public class VerifiedJwtImpl implements VerifiedJwt {
    private String header;
    private String payload;
    private String issuer;
    private String username;

    @Override
    public String getUserId() {
        return userId;
    }

    private String userId;
    private Integer roles;
    private Date expiresAt;

    public VerifiedJwtImpl(DecodedJWT decodedJWT) {
        this.header = decodedJWT.getHeader();
        this.payload = decodedJWT.getPayload();
        this.issuer = decodedJWT.getIssuer();
        this.expiresAt = decodedJWT.getExpiresAt();
        this.username = decodedJWT.getClaim("username").asString();
        this.roles = decodedJWT.getClaim("roles").asInt();
        this.userId = decodedJWT.getClaim("userId").asString();
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public Date getExpiresAt() {
        return expiresAt;
    }
    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }
    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public Integer getRoles() {
        return roles;
    }
}
