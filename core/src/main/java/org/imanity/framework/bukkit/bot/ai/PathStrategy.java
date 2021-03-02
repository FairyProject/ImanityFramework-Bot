package org.imanity.framework.bukkit.bot.ai;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface PathStrategy {
    void clearCancelReason();

    CancelReason getCancelReason();

    Iterable<Vector> getPath();

    Location getTargetAsLocation();

    TargetType getTargetType();

    void stop();

    boolean update();
}
