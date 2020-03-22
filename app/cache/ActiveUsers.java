package cache;

import dbobjects.user.User;
import dbobjects.user.UserRepository;
import play.cache.NamedCache;
import play.cache.redis.AsyncCacheApi;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;

public class ActiveUsers {
    @Inject
    @NamedCache("activeUsers")
    private AsyncCacheApi activeUsers;

    @Inject
    public ActiveUsers(AsyncCacheApi activeUsers) {
        this.activeUsers = activeUsers;
    }

    public void activateUser(User user) {
        activeUsers.set(user.getIdString(), user, 60 * 30);
    }
}
