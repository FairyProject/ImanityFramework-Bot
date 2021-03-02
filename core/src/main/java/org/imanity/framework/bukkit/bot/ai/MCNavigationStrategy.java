package org.imanity.framework.bukkit.bot.ai;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.imanity.framework.bukkit.bot.Bot;

import java.util.List;

public class MCNavigationStrategy extends AbstractPathStrategy {
    private final Entity handle;
    private final Bot bot;
    private final MCNavigator navigator;
    private final Location target;

    public MCNavigationStrategy(final Bot npc, Iterable<Vector> path) {
        super(TargetType.LOCATION);
        List<Vector> list = Lists.newArrayList(path);
        this.target = list.get(list.size() - 1).toLocation(npc.getBukkitEntity().getWorld());
        this.bot = npc;
        handle = npc.getBukkitEntity();
        this.navigator = npc.navigator(list);
    }

    public MCNavigationStrategy(final Bot npc, Location dest) {
        super(TargetType.LOCATION);
        this.target = dest;
        handle = npc.getBukkitEntity();
        this.bot = npc;
        this.navigator = npc.navigator(dest);
    }

    @Override
    public Iterable<Vector> getPath() {
        return navigator.getPath();
    }

    @Override
    public Location getTargetAsLocation() {
        return target;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.LOCATION;
    }

    @Override
    public void stop() {
        navigator.stop();
    }

    @Override
    public String toString() {
        return "MCNavigationStrategy [target=" + target + "]";
    }

    @Override
    public boolean update() {
        if (navigator.getCancelReason() != null) {
            setCancelReason(navigator.getCancelReason());
        }
        if (getCancelReason() != null) {
            this.bot.behaviour().callbacks().forEach(callback -> callback.onCompletion(getCancelReason()));
            return true;
        }
        boolean wasFinished = navigator.update();
        Location loc = handle.getLocation(HANDLE_LOCATION);
        double dX = target.getBlockX() + 0.5 - loc.getX();
        double dZ = target.getBlockZ() + 0.5 - loc.getZ();
        double dY = target.getY() - loc.getY();
        double xzDistance = dX * dX + dZ * dZ;
        if ((dY * dY) < 1 && xzDistance <= 2.0) {
            stop();
            return true;
        }
        if (navigator.getCancelReason() != null) {
            setCancelReason(navigator.getCancelReason());
            return true;
        }
        return wasFinished;
    }

    public static interface MCNavigator {
        CancelReason getCancelReason();

        Iterable<Vector> getPath();

        void stop();

        boolean update();
    }

    private static final Location HANDLE_LOCATION = new Location(null, 0, 0, 0);
}
