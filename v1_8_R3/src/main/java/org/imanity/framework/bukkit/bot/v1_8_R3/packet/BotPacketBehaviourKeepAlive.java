package org.imanity.framework.bukkit.bot.v1_8_R3.packet;

import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;
import org.imanity.framework.bukkit.bot.v1_8_R3.connection.BotPlayerConnection;
import org.imanity.framework.reflect.ReflectObject;

public class BotPacketBehaviourKeepAlive implements BotPacketBehaviour<PacketPlayOutKeepAlive> {
    @Override
    public void handle(EntityBot bot, BotPlayerConnection playerConnection, PacketPlayOutKeepAlive packet) {
        ReflectObject outReflectObject = new ReflectObject(packet);

        PacketPlayInKeepAlive keepAlive = new PacketPlayInKeepAlive();
        ReflectObject reflectObject = new ReflectObject(keepAlive);
        reflectObject.set("a", outReflectObject.get("a"));

        playerConnection.receivePacket(keepAlive);
    }
}
