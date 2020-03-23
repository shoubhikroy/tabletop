package cache;


import dbobjects.user.User;
import dbobjects.user.UserRepository;
import play.Logger;
import play.cache.NamedCache;
import play.cache.redis.AsyncCacheApi;
import scala.Option;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ActiveUsers {
    @NamedCache("activeUsers")
    private AsyncCacheApi activeUsers;

    private final UserRepository userRepository;

    private final CacheExecutionContext executionContext;

    @Inject
    public ActiveUsers(AsyncCacheApi activeUsers, UserRepository userRepository, CacheExecutionContext executionContext) {
        this.activeUsers = activeUsers;
        this.userRepository = userRepository;
        this.executionContext = executionContext;
    }

    public CompletionStage<Boolean> activate(String userId) {
        return activeUsers.get(userId).thenApplyAsync(_fromCache -> {
            if (_fromCache.isPresent()) {
                activeUsers.set("user." + userId, _fromCache.get(), 60 * 5).thenApplyAsync((done) -> {
                    Logger.info("readding user to cache: " + userId);
                    return true;
                });
            } else {
                userRepository.get(Long.valueOf(userId)).thenApplyAsync(user -> {
                    if (user.isPresent()) activeUsers.set("user." + userId, user.get(), 60 * 5).thenApplyAsync((done) -> {
                        Logger.info("adding user to cache: " + userId);
                        return true;
                    });
                    return false;
                });
            }
            return false;
        }, executionContext);
    }

    public CompletionStage<List<Optional<User>>> getActiveUsers() {
        return getActiveUsersKeys().thenApplyAsync(_users -> {
                return activeUsers.getAll(User.class, _users).toCompletableFuture().join();
        }, executionContext);
    }

    public CompletionStage<List<String>> getActiveUsersKeys() {
        return activeUsers.matching("user*").thenApplyAsync(users -> users, executionContext);
    }
}
