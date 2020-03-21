package dbobjects.user;

import dbobjects.DBObjectRepository;
import dbobjects.DatabaseExecutionContext;
import org.mindrot.jbcrypt.BCrypt;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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

    public static String createPassword(String clearString) {
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) {
            return false;
        }
        if (encryptedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}
