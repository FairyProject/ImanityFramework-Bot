package org.imanity.framework.bukkit.bot.v1_8_R3.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;

@UtilityClass
public class NMSUtil {

    public static void flyingMoveLogic(EntityBot entity, float f, float f1) {
        if (entity.bM()) {
            if ((entity.V())) {
                double d0 = entity.locY;
                float f3 = 0.8F;
                float f4 = 0.02F;
                float f2 = EnchantmentManager.b(entity);
                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }
                if (!entity.onGround) {
                    f2 *= 0.5F;
                }
                if (f2 > 0.0F) {
                    f3 += (0.54600006F - f3) * f2 / 3.0F;
                    f4 += (entity.bI() * 1.0F - f4) * f2 / 3.0F;
                }
                entity.a(f, f1, f4);
                entity.movePacketFly(entity.motX, entity.motY, entity.motZ);
                entity.motX *= f3;
                entity.motY *= 0.800000011920929D;
                entity.motZ *= f3;
                entity.motY -= 0.02D;
                if ((entity.positionChanged)
                        && (entity.c(entity.motX, entity.motY + 0.6000000238418579D - entity.locY + d0, entity.motZ))) {
                    entity.motY = 0.30000001192092896D;
                }
            } else if ((entity.ab())) {
                double d0 = entity.locY;
                entity.a(f, f1, 0.02F);
                entity.movePacketFly(entity.motX, entity.motY, entity.motZ);
                entity.motX *= 0.5D;
                entity.motY *= 0.5D;
                entity.motZ *= 0.5D;
                entity.motY -= 0.02D;
                if ((entity.positionChanged)
                        && (entity.c(entity.motX, entity.motY + 0.6000000238418579D - entity.locY + d0, entity.motZ))) {
                    entity.motY = 0.30000001192092896D;
                }
            } else {
                float f5 = 0.91F;
                if (entity.onGround) {
                    f5 = entity.world
                            .getType(new BlockPosition(MathHelper.floor(entity.locX),
                                    MathHelper.floor(entity.getBoundingBox().b) - 1, MathHelper.floor(entity.locZ)))
                            .getBlock().frictionFactor * 0.91F;
                }
                float f6 = 0.16277136F / (f5 * f5 * f5);
                float f3;
                if (entity.onGround) {
                    f3 = entity.bI() * f6;
                } else {
                    f3 = entity.aM;
                }
                entity.a(f, f1, f3);
                f5 = 0.91F;
                if (entity.onGround) {
                    f5 = entity.world
                            .getType(new BlockPosition(MathHelper.floor(entity.locX),
                                    MathHelper.floor(entity.getBoundingBox().b) - 1, MathHelper.floor(entity.locZ)))
                            .getBlock().frictionFactor * 0.91F;
                }
                if (entity.k_()) {
                    float f4 = 0.15F;
                    entity.motX = MathHelper.a(entity.motX, -f4, f4);
                    entity.motZ = MathHelper.a(entity.motZ, -f4, f4);
                    entity.fallDistance = 0.0F;
                    if (entity.motY < -0.15D) {
                        entity.motY = -0.15D;
                    }
                    boolean flag = (entity.isSneaking()) && ((entity instanceof EntityHuman));
                    if ((flag) && (entity.motY < 0.0D)) {
                        entity.motY = 0.0D;
                    }
                }
                entity.movePacketFly(entity.motX, entity.motY, entity.motZ);
                if ((entity.positionChanged) && (entity.k_())) {
                    entity.motY = 0.2D;
                }
                if ((entity.world.isClientSide) && ((!entity.world
                        .isLoaded(new BlockPosition((int) entity.locX, 0, (int) entity.locZ)))
                        || (!entity.world
                        .getChunkAtWorldCoords(new BlockPosition((int) entity.locX, 0, (int) entity.locZ))
                        .o()))) {
                    if (entity.locY > 0.0D) {
                        entity.motY = -0.1D;
                    } else {
                        entity.motY = 0.0D;
                    }
                } else {
                    entity.motY -= 0.08D;
                }
                entity.motY *= 0.9800000190734863D;
                entity.motX *= f5;
                entity.motZ *= f5;
            }
        }
        entity.aA = entity.aB;
        double d0 = entity.locX - entity.lastX;
        double d1 = entity.locZ - entity.lastZ;

        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
        if (f2 > 1.0F) {
            f2 = 1.0F;
        }
        entity.aB += (f2 - entity.aB) * 0.4F;
        entity.aC += entity.aB;
    }

    public void setHeadYaw(EntityLiving entity, float yaw) {
        yaw = clampYaw(yaw);
        entity.aJ = yaw;
        if (!(entity instanceof EntityHuman))
            entity.aI = yaw;
        entity.aK = yaw;
    }

    private float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    private static final Location AT_LOCATION = new Location(null, 0, 0, 0);

    public static boolean isLoaded(Location location) {
        if (location.getWorld() == null)
            return false;
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        return location.getWorld().isChunkLoaded(chunkX, chunkZ);
    }

    public boolean inBlock(Entity entity) {
        // TODO: bounding box aware?
        Location loc = entity.getLocation(AT_LOCATION);
        if (!isLoaded(loc)) {
            return false;
        }
        Block in = loc.getBlock();
        return in.getType().isSolid() && in.getRelative(BlockFace.UP).getType().isSolid();
    }

}
