package org.imanity.framework.bukkit.bot.v1_8_R3.controller;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntitySenses;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;

import java.util.ArrayList;
import java.util.List;

public class BotSenses
        extends EntitySenses {
    EntityBot entity;
    List<Entity> seenEntities = new ArrayList<>();
    List<Entity> unseenEntities = new ArrayList<>();

    public BotSenses(EntityBot bot) {
        super(new EntitySheep(bot.world));
        this.entity = bot;
    }

    public void a() {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    public boolean a(Entity entity) {
        if (this.seenEntities.contains(entity)) {
            return true;
        }
        if (this.unseenEntities.contains(entity)) {
            return false;
        }
        this.entity.world.methodProfiler.a("canSee");
        boolean flag = this.entity.hasLineOfSight(entity);

        this.entity.world.methodProfiler.b();
        if (flag) {
            this.seenEntities.add(entity);
        } else {
            this.unseenEntities.add(entity);
        }
        return flag;
    }
}