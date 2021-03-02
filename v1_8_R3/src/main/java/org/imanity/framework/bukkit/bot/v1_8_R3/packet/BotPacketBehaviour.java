package org.imanity.framework.bukkit.bot.v1_8_R3.packet;

import net.minecraft.server.v1_8_R3.Packet;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;
import org.imanity.framework.bukkit.bot.v1_8_R3.connection.BotPlayerConnection;

public interface BotPacketBehaviour<T extends Packet> {

    void handle(EntityBot bot, BotPlayerConnection playerConnection, T packet);

}
