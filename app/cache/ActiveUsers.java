package cache;


import dbobjects.user.User;
import dbobjects.user.UserRepository;
import play.Logger;
import play.cache.NamedCache;
import play.cache.redis.AsyncCacheApi;

import javax.inject.Inject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

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

    public Boolean activate(User user) {
        Logger.info("adding user to cache: " + user.getUsername());
        activeUsers.set(user.getIdString(), user, 60 * 1);
        return true;
    }

    public CompletionStage<Boolean> activate(String userId) {
        return activeUsers.get(userId).thenApplyAsync(_fromCache -> {
            if (_fromCache.isPresent()) {
                activeUsers.set(userId, _fromCache.get(), 60 * 1).thenApplyAsync((done) -> {
                    Logger.info("readding user to cache: " + userId);
                    return true;
                });
            } else {
                userRepository.get(Long.valueOf(userId)).thenApplyAsync(user -> {
                    if (user.isPresent()) activeUsers.set(userId, user.get(), 60 * 1).thenApplyAsync((done) -> {
                        Logger.info("adding user to cache: " + userId);
                        return true;
                    });
                    return false;
                });
            }
            return false;
        }, executionContext);
    }
}
