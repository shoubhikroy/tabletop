package dbobjects.user;

import dbobjects.DBObjectRepository;
import dbobjects.DatabaseExecutionContext;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    private Optional<User> findByName(EntityManager em, String username) {
        try {
            User u = em.createQuery(
                    "SELECT u from User u WHERE u.username = :username", User.class).
                    setParameter("username", username).getSingleResult();
            return Optional.ofNullable(u);
        } catch (NoResultException e) {
            Logger.error("NotFound");
            return Optional.ofNullable(null);
        }
    }

    public CompletionStage<Optional<User>> findByName(String username) {
        return supplyAsync(() -> wrap(em -> findByName(em, username)), executionContext);
    }

    public CompletionStage<Optional<User>> checkUserPass(String username, String password) {
        return supplyAsync(() -> {
            Optional<User> u = wrap(em -> findByName(em, username));
            if (u.isPresent()) {
                User _u =  u.get();
                String encPass = _u.getPassword();
                if(checkPassword(password, encPass)) return u;
            }
            return Optional.ofNullable(null);
        }, executionContext);
    }
}
