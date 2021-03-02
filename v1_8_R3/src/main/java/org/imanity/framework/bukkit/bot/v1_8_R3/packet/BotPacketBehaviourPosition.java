package org.imanity.framework.bukkit.bot.v1_8_R3.packet;

import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;
import org.imanity.framework.bukkit.bot.v1_8_R3.connection.BotPlayerConnection;
import org.imanity.framework.reflect.ReflectObject;

public class BotPacketBehaviourPosition implements BotPacketBehaviour<PacketPlayOutPosition> {

    @Override
    public void handle(EntityBot bot, BotPlayerConnection playerConnection, PacketPlayOutPosition packet) {
        ReflectObject reflectObject = new ReflectObject(packet);

        bot.setLocation(
                reflectObject.get("a"),
                reflectObject.get("b"),
                reflectObject.get("c"),
                reflectObject.get("d"),
                reflectObject.get("e")
        );
    }

}
