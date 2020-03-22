package jwt;

import java.util.Date;

public interface VerifiedJwt {
    String getHeader();
    String getPayload();
    String getIssuer();
    String getUsername();
    String getUserId();
    Integer getRoles();
    Date getExpiresAt();
}
