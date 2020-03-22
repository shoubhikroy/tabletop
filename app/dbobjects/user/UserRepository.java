package dbobjects.user;

import dbobjects.DBObjectRepository;
import dbobjects.DatabaseExecutionContext;
import org.mindrot.jbcrypt.BCrypt;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class UserRepository extends DBObjectRepository<User> {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public UserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext, User.class);
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    private Optional<User> findByName(EntityManager em, String username) {
        User u = em.createQuery(
                "SELECT u from User u WHERE u.username = :username", User.class).
                setParameter("username", username).getSingleResult();
        return Optional.ofNullable(u);
    }

    static String createPassword(String clearString) {
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    private static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) {
            return false;
        }
        if (encryptedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encryptedPassword);
    }

    public CompletionStage<Optional<User>> checkUserPass(String username, String password) {
        return supplyAsync(() -> {
            Optional<User> u = wrap(em -> findByName(em, username));
            User _u =  u.get();
            String encPass = _u.getPassword();
            if(checkPassword(password, encPass)) return u;
            else return Optional.ofNullable(null);
        }, executionContext);
    }
}
