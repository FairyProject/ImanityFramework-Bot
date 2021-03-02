package org.imanity.framework.bukkit.bot;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public interface BotFactoryBuilder {

    CompletableFuture<Bot> generateBot(UUID uuid, String name);

}
