package dbobjects;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface DBObjectRepository<T> {

    CompletionStage<Stream<T>> list();

    CompletionStage<Optional<T>> create(T postData);

    CompletionStage<Optional<T>> get(Long id);

    CompletionStage<Optional<T>> update(Long id, T object);

    CompletionStage<Optional<T>> delete(Long id, T object);
}
