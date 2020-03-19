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

public class UserRepository implements DBObjectRepository<User> {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public UserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Optional<User>> create(User user) {
        return supplyAsync(() -> wrap(em -> insert(em, user)), executionContext);
    }

    @Override
    public CompletionStage<Stream<User>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Optional<User>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Optional<User>> update(Long id, User user) {
        //return supplyAsync(() -> wrap(em -> modify(em, id, user)), executionContext);
        return null;
    }

    @Override
    public CompletionStage<Optional<User>> delete(Long id, User object) {
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<User> insert(EntityManager em, User user) {
        em.persist(user);
        return Optional.ofNullable(user);
    }

    private Optional<User> get(EntityManager em, Long userId) {
        User user = em.find(User.class, new Long(userId));
        if (user != null) {
            em.detach(user);
        }
        return Optional.ofNullable(user);
    }

    private Stream<User> list(EntityManager em) {
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        return users.stream();
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
