package cache;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context wired to "contexts.blockingCacheDispatcher" thread pool
 */
public class CacheExecutionContext extends CustomExecutionContext {
    @Inject
    public CacheExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "contexts.blockingCacheDispatcher");
    }
}
