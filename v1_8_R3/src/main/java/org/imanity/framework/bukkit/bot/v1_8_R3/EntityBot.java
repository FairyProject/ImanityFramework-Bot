package org.imanity.framework.bukkit.bot.v1_8_R3;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.imanity.framework.Autowired;
import org.imanity.framework.bukkit.Imanity;
import org.imanity.framework.bukkit.bot.Bot;
import org.imanity.framework.bukkit.bot.BotBehaviour;
import org.imanity.framework.bukkit.bot.BotFactory;
import org.imanity.framework.bukkit.bot.ai.CancelReason;
import org.imanity.framework.bukkit.bot.ai.MCNavigationStrategy;
import org.imanity.framework.bukkit.bot.ai.NavigatorCallback;
import org.imanity.framework.bukkit.bot.ai.PathStrategy;
import org.imanity.framework.bukkit.bot.v1_8_R3.controller.PlayerControllerJump;
import org.imanity.framework.bukkit.bot.v1_8_R3.controller.PlayerControllerLook;
import org.imanity.framework.bukkit.bot.v1_8_R3.controller.PlayerControllerMove;
import org.imanity.framework.bukkit.bot.v1_8_R3.controller.PlayerNavigation;
import org.imanity.framework.bukkit.bot.v1_8_R3.util.NMSUtil;
import org.imanity.framework.bukkit.reflection.resolver.FieldResolver;
import org.imanity.framework.bukkit.reflection.wrapper.FieldWrapper;
import org.imanity.framework.bukkit.util.TaskUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityBot extends EntityPlayer implements Bot {

    private static final FieldWrapper<WorldServer> NAVIGATION_WORLD_FIELD = new FieldResolver(NavigationAbstract.class).resolveWrapper("c");

    private static final float EPSILON = 0.005F;

    @Autowired
    private static Optional<BotFactory> BOT_FACTORY;

    private final PlayerControllerLook lookController;
    private final PlayerControllerMove moveController;
    private final PlayerControllerJump jumpController;
    private final PlayerNavigation navigation;

    private PathStrategy executing;

    public EntityBot(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(minecraftserver, worldserver, gameprofile, playerinteractmanager);

        this.lookController = new PlayerControllerLook(this);
        this.moveController = new PlayerControllerMove(this);
        this.jumpController = new PlayerControllerJump(this);

        AttributeInstance range = getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        if (range == null) {
            range = getAttributeMap().b(GenericAttributes.FOLLOW_RANGE);
        }
        range.setValue(25F);

        this.navigation = new PlayerNavigation(this, this.world);

        getNavigation().b(true);
        getNavigation().a(true);

        invulnerableTicks = 0;
        S = 0;
    }

    public PlayerControllerLook getControllerLook() {
        return this.lookController;
    }

    public PlayerControllerMove getControllerMove() {
        return this.moveController;
    }

    public PlayerControllerJump getControllerJump() {
        return this.jumpController;
    }

    public PlayerNavigation getNavigation() {
        return this.navigation;
    }

    public void playArmAnimation() {
        PacketPlayInArmAnimation packet = new PacketPlayInArmAnimation();
        this.playerConnection.a(packet);
    }

    @Override
    public BotBehaviour behaviour() {
        return new BotBehaviour() {
            @Override
            public boolean avoidWater() {
                return true;
            }

            @Override
            public double pathDistanceMargin() {
                return 1;
            }

            @Override
            public float speed() {
                return 1;
            }

            @Override
            public List<NavigatorCallback> callbacks() {
                return Arrays.asList(var1 -> System.out.println("DONE " + var1.name()));
            }

            @Override
            public int range() {
                return 64;
            }
        };
    }

    @Override
    public void die(DamageSource damagesource) {
        if (!this.dead) {
            TaskUtil.runScheduled(() -> {
                PacketPlayInClientCommand in = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
                this.playerConnection.a(in);
            }, 20L);
        }
        super.die(damagesource);
    }

    @Override
    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
//        super.a(d0, flag, block, blockposition);
        super.a(d0, flag);
    }

    public void t_() {
        super.t_();
//        this.W();

//        if (motX != 0 || motY != 0 || motZ != 0 || !this.firstMovement) {
//            this.firstMovement = true;
//            g(0, 0);
//        }
    }

    @Override
    public void l() {
        super.l();

        if (Math.abs(motX) < EPSILON && Math.abs(motY) < EPSILON && Math.abs(motZ) < EPSILON) {
            motX = motY = motZ = 0;
        }

        if (!this.navigation.m()) {
            this.navigation.k();

            this.moveOnCurrentHeading();
        }

        this.D();
        this.moveController.c();
        this.jumpController.b();
        this.lookController.a();

        if (this.executing != null) {
            this.executing.update();
        }
    }

    private int jumpTicks = 0;

    private void moveOnCurrentHeading() {
//        if (aY) {
//            if (onGround && jumpTicks == 0) {
//                bF();
//                jumpTicks = 10;
//            }
//        } else {
//            jumpTicks = 0;
//        }
        aZ *= 0.98F;
        ba *= 0.98F;
        bb *= 0.9F;
//        g(aZ, ba); // movement method
        NMSUtil.setHeadYaw(this, yaw);
        if (jumpTicks > 0) {
            jumpTicks--;
        }
    }

    @Override
    public void bF() {
        super.bF();
    }

    public void setMoveDestination(double x, double y, double z, double speed) {
        this.moveController.a(x, y, z, speed);
    }

    @Override
    public void g(float f, float f1) {
        super.g(f, f1);
//        NMSUtil.flyingMoveLogic(this, f, f1);
    }

    @Override
    public boolean W() {
        if (this.world.a(this.getBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D, 0.001D, 0.001D), Material.WATER, this)) {
            if (!this.inWater && !this.justCreated) {
                this.X();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fireTicks = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    @Override
    public boolean inBlock() {
        return NMSUtil.inBlock(this.getBukkitEntity());
    }

    @Override
    public void move(double d0, double d1, double d2) {
        Location location = this.getBukkitEntity().getLocation();
        super.move(d0, d1, d2);
        Location to = this.getBukkitEntity().getLocation();

        if (!Imanity.IMPLEMENTATION.callMoveEvent(this.getBukkitEntity(), location, to)) {
            this.getBukkitEntity().teleport(location);
        }
    }

    public void n(float f) {
        this.bf = f;
    }

    public void movePacketFly(double x, double y, double z) {
        this.move(x, y, z);
    }

    @Override
    public void disconnect(String message) {
        final ChatComponentText chatcomponenttext = new ChatComponentText(message);
        this.playerConnection.a(chatcomponenttext);

        this.world.removeEntity(this);

        BOT_FACTORY.ifPresent(botFactory -> botFactory.removeBot(this));
    }

    @Override
    public MCNavigationStrategy.MCNavigator navigator(List<Vector> dest) {
        final PathEntity path = new PathEntity(
                Iterables.toArray(dest.stream()
                        .map(input -> new PathPoint(input.getBlockX(), input.getBlockY(), input.getBlockZ()))
                        .collect(Collectors.toList()), PathPoint.class));
        return navigator(input -> input.a(path, behaviour().speed()));
    }

    @Override
    public MCNavigationStrategy.MCNavigator navigator(Location dest) {
        return navigator(input -> input.a(dest.getX(), dest.getY(), dest.getZ(), behaviour().speed()));
    }

    public MCNavigationStrategy.MCNavigator navigator(Function<NavigationAbstract, Boolean> function) {
        this.onGround = true;
        // not sure of a better way around this - if onGround is false, then
        // navigation won't execute, and calling entity.move doesn't
        // entirely fix the problem.
        final NavigationAbstract navigation = this.getNavigation();
        return new MCNavigationStrategy.MCNavigator() {
            float lastSpeed;
            CancelReason reason;

            @Override
            public CancelReason getCancelReason() {
                return reason;
            }

            @Override
            public Iterable<Vector> getPath() {
                return new NavigationIterable(navigation);
            }

            @Override
            public void stop() {
                navigation.n();
            }

            @Override
            public boolean update() {
                if (behaviour().speed() != lastSpeed) {
                    Entity handle = EntityBot.this;
                    float oldWidth = handle.width;
                    if (!function.apply(navigation)) {
                        reason = CancelReason.STUCK;
                    }
                    handle.width = oldWidth; // minecraft requires that an entity fit onto both blocks if width >= 1f,
                    // but we'd prefer to make it just fit on 1 so hack around it a bit.
                    lastSpeed = behaviour().speed();
                }
                navigation.a(behaviour().speed());
                return navigation.m();
            }
        };
    }

    private static class NavigationIterable implements Iterable<Vector> {
        private final NavigationAbstract navigation;

        public NavigationIterable(NavigationAbstract nav) {
            this.navigation = nav;
        }

        @Override
        public @NotNull Iterator<Vector> iterator() {
            final int npoints = navigation.j() == null ? 0 : navigation.j().d();
            return new Iterator<Vector>() {
                PathPoint curr = npoints > 0 ? navigation.j().a(0) : null;
                int i = 0;

                @Override
                public boolean hasNext() {
                    return curr != null;
                }

                @Override
                public Vector next() {
                    PathPoint old = curr;
                    curr = i + 1 < npoints ? navigation.j().a(++i) : null;
                    return new Vector(old.a, old.b, old.c);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    @Override
    public void setTarget(Location target) {
        setTarget(target, new MCNavigationStrategy(this, target.clone()));
    }

    public void setTarget(Location target, PathStrategy strategy) {
//        if (!npc.isSpawned())
//            throw new IllegalStateException("npc is not spawned");
        if (target == null) {
            cancelNavigation();
            return;
        }
        target = target.clone();
        switchStrategyTo(strategy);
    }

    private void switchStrategyTo(PathStrategy newStrategy) {
        updatePathfindingRange(this.behaviour().range());
//        if (executing != null) {
//            Bukkit.getPluginManager().callEvent(new NavigationReplaceEvent(this));
//        }
        executing = newStrategy;
//        stationaryTicks = 0;
//        if (npc.isSpawned()) {
//            NMS.updateNavigationWorld(npc.getEntity(), npc.getEntity().getWorld());
//        }
//        Bukkit.getPluginManager().callEvent(new NavigationBeginEvent(this));
    }

    public void updatePathfindingRange(float pathfindingRange) {
        this.navigation.setRange(pathfindingRange);
    }

    public boolean isNavigating() {
        return executing != null;
    }

    private void stopNavigating(CancelReason reason) {
        if (!isNavigating())
            return;
        // TODO: callback
        Iterator<NavigatorCallback> itr = behaviour().callbacks().iterator();//localParams.callbacks().iterator();
        List<NavigatorCallback> callbacks = new ArrayList<NavigatorCallback>();
        while (itr.hasNext()) {
            callbacks.add(itr.next());
            itr.remove();
        }
        for (NavigatorCallback callback : callbacks) {
            callback.onCompletion(reason);
        }
        if (reason == null) {
            stopNavigating();
            return;
        }
//        if (reason == CancelReason.STUCK) {
//            StuckAction action = localParams.stuckAction();
//            NavigationStuckEvent event = new NavigationStuckEvent(this, action);
//            Bukkit.getPluginManager().callEvent(event);
//            action = event.getAction();
//            boolean shouldContinue = action != null ? action.run(npc, this) : false;
//            if (shouldContinue) {
//                stationaryTicks = 0;
//                executing.clearCancelReason();
//                return;
//            }
//        }
//        NavigationCancelEvent event = new NavigationCancelEvent(this, reason);
//        PathStrategy old = executing;
//        Bukkit.getPluginManager().callEvent(event);
//        if (old == executing) {
//            stopNavigating();
//        }
    }

    public void cancelNavigation() {
        stopNavigating(CancelReason.PLUGIN);
    }

    private void stopNavigating() {
        if (executing != null) {
            executing.stop();
        }
        executing = null;

//        stationaryTicks = 0;
//        if (npc.isSpawned()) {
            Vector velocity = this.getBukkitEntity().getVelocity();
            velocity.setX(0).setY(0).setZ(0);
            this.getBukkitEntity().setVelocity(velocity);
//        }
//        Location loc = npc.getEntity().getLocation(STATIONARY_LOCATION);
//        NMS.look(npc.getEntity(), loc.getYaw(), 0);
    }

    @Override
    public void tick() {
        this.l();
    }
}
