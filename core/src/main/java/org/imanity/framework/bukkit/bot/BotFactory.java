package org.imanity.framework.bukkit.bot;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.imanity.framework.*;
import org.imanity.framework.bukkit.Imanity;
import org.imanity.framework.bukkit.metadata.Metadata;
import org.imanity.framework.bukkit.util.TaskUtil;
import org.imanity.framework.metadata.MetadataKey;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service(name = "bot")
public class BotFactory {

    public static boolean ENABLED = false;

    public static BotFactory INSTANCE;
    public static String METADATA_KEY = "ImanityBot";
    public static MetadataKey<Bot> METADATA = MetadataKey.create(METADATA_KEY, Bot.class);

    private static final Logger LOGGER = LogManager.getLogger();

    private AtomicInteger idCounter;
    private Map<UUID, Bot> bots;
    private BotFactoryBuilder factoryBuilder;

    @ShouldInitialize
    public boolean shouldInit() {
        ENABLED = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
        if (!ENABLED) {
            LOGGER.info("Due to ProtocolLib not installed, the BotFactory has been disabled.");
        }
        return ENABLED;
    }

    @PreInitialize
    public void preInit() {
        INSTANCE = this;
        this.bots = new ConcurrentHashMap<>();
        this.idCounter = new AtomicInteger(0);

        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public Class<?>[] type() {
                return new Class[] { BotFactoryBuilder.class };
            }

            @Override
            public void onEnable(Object instance) {
                if (factoryBuilder != null) {
                    throw new IllegalArgumentException("There is more than 1 BotFactoryBuilder being registered!");
                }
                factoryBuilder = (BotFactoryBuilder) instance;
            }
        });
    }

    @PostInitialize
    public void postInit() {
        if (factoryBuilder == null) {
            throw new IllegalArgumentException("Your current version does not support BotFactory!");
        }

        TaskUtil.runRepeated(() -> this.getBots().forEach(Bot::tick), 1L);
    }

    @PreDestroy
    public void preDestroy() {
        this.getBots().forEach(bot -> bot.disconnect("Server Closed"));
    }

    public CompletableFuture<Bot> generateRandomly() {
        return this.generate(this.getRandomUuid(), this.getRandomName());
    }

    public String getRandomName() {
        return "Bot-" + this.idCounter.getAndIncrement();
    }

    public CompletableFuture<Bot> generate(UUID uuid, String name) {
        return this.factoryBuilder.generateBot(uuid, name);
    }

    public void addBot(Bot bot) {
        this.bots.put(bot.getUniqueID(), bot);
    }

    public void setBotMetadata(Bot bot) {
        final Player bukkitEntity = bot.getBukkitEntity();
        Metadata.provideForPlayer(bukkitEntity).put(METADATA, bot);
        bukkitEntity.setMetadata(METADATA_KEY, new FixedMetadataValue(Imanity.PLUGIN, bot));
    }

    public void removeBot(Bot bot) {
        this.bots.remove(bot.getUniqueID());
        final Player bukkitEntity = bot.getBukkitEntity();
        Metadata.provideForPlayer(bukkitEntity).remove(METADATA);
        bukkitEntity.removeMetadata(METADATA_KEY, Imanity.PLUGIN);
    }

    public boolean isBot(UUID uuid) {
        return this.bots.containsKey(uuid);
    }

    public Collection<Bot> getBots() {
        return ImmutableList.copyOf(this.bots.values());
    }

    public UUID getRandomUuid() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.isBot(uuid) || Bukkit.getPlayer(uuid) != null);

        return uuid;
    }

}
