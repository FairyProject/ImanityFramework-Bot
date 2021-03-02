package org.imanity.framework.bukkit.bot;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.imanity.framework.bukkit.bot.ai.MCNavigationStrategy;

import java.util.List;
import java.util.UUID;

public interface Bot {

    default void disconnect() {
        this.disconnect("Disconnected");
    }

    void disconnect(String message);

    Player getBukkitEntity();

    UUID getUniqueID();

    void setMoveDestination(double x, double y, double z, double speed);

    BotBehaviour behaviour();

    MCNavigationStrategy.MCNavigator navigator(List<Vector> list);

    MCNavigationStrategy.MCNavigator navigator(Location dest);

    void setTarget(Location target);

    void tick();

}
