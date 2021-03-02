package org.imanity.framework.bukkit.bot.ai;

public enum CancelReason {
    NPC_DESPAWNED,
    PLUGIN,
    REPLACE,
    STUCK,
    TARGET_DIED,
    TARGET_MOVED_WORLD;

    private CancelReason() {
    }
}
