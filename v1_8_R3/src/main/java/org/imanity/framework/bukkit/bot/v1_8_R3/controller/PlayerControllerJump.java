package org.imanity.framework.bukkit.bot.v1_8_R3.controller;

import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;

public class PlayerControllerJump {
    private final EntityBot a;
    private boolean b;

    public PlayerControllerJump(EntityBot entityinsentient) {
        this.a = entityinsentient;
    }

    public void a() {
        this.b = true;
    }

    public void b() {
        this.a.i(this.b);
        this.b = false;
    }
}
