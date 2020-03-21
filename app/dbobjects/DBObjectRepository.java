package dbobjects;

import org.hibernate.exception.ConstraintViolationException;
import play.Logger;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public abstract class DBObjectRepository<T extends DBOject> {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;
    final Class<T> typeParamClass;

    public DBObjectRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext, Class<T> typeParamClass) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
        this.typeParamClass = typeParamClass;
    }


    public CompletionStage<Optional<T>> create(T object) {
        return supplyAsync(() -> wrap(em -> insert(em, object)), executionContext);
    }

    public CompletionStage<Stream<T>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    public CompletionStage<Optional<T>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    public CompletionStage<Optional<T>> update(T object) {
        return supplyAsync(() -> wrap(em -> update(em, object)), executionContext);
    }

    public CompletionStage<Optional<T>> delete(T object) {
        return supplyAsync(() -> wrap(em -> {
                return delete(em, object);
        }), executionContext);
    }

    private Optional<T> delete(EntityManager em, T object) {
        object.setDeleted(true);
        em.merge(object);
        return Optional.ofNullable(object);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<T> insert(EntityManager em, T object) {
        em.persist(object);
        return Optional.ofNullable(object);
    }

    private Optional<T> update(EntityManager em, T object) {
        em.merge(object);
        return Optional.ofNullable(object);
    }

    private Optional<T> get(EntityManager em, Long objectId) {
        T object = em.find(typeParamClass, Long.valueOf(objectId));
        if (object != null) {
            em.detach(object);
        }
        return Optional.ofNullable(object);
    }

    private Stream<T> list(EntityManager em) {
        List<T> objects = em.createQuery(MessageFormat.format("select u from {0} u WHERE u.deleted = true", typeParamClass.getSimpleName()), typeParamClass).getResultList();
        return objects.stream();
    }

}
