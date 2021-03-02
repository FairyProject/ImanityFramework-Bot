package org.imanity.framework.bukkit.bot.v1_8_R3.connection;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerKickEvent;
import org.imanity.framework.bukkit.bot.v1_8_R3.EntityBot;
import org.imanity.framework.bukkit.bot.v1_8_R3.packet.BotPacketBehaviour;
import org.imanity.framework.bukkit.bot.v1_8_R3.packet.BotPacketBehaviourKeepAlive;
import org.imanity.framework.bukkit.bot.v1_8_R3.packet.BotPacketBehaviourPosition;
import org.imanity.framework.util.Stacktrace;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotPlayerConnection
        extends PlayerConnection {

    private static final Map<Class<? extends Packet>, BotPacketBehaviour<?>> PACKET_BEHAVIOURS = new ConcurrentHashMap<>();
    private static Method NETWORK_MANAGER_RECEIVE_METHOD;

    static {
        PACKET_BEHAVIOURS.put(PacketPlayOutPosition.class, new BotPacketBehaviourPosition());
        PACKET_BEHAVIOURS.put(PacketPlayOutKeepAlive.class, new BotPacketBehaviourKeepAlive());

        try {
            NETWORK_MANAGER_RECEIVE_METHOD = NetworkManager.class.getDeclaredMethod("a", ChannelHandlerContext.class, Packet.class);
            NETWORK_MANAGER_RECEIVE_METHOD.setAccessible(true);
        } catch (Throwable throwable) {
            Stacktrace.print(throwable);
        }
    }

    private final EntityBot entityBot;

    public BotPlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityBot entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
        this.entityBot = entityplayer;
    }

    public CraftPlayer getPlayer() {
        return this.player == null ? null : this.player.getBukkitEntity();
    }

    public NetworkManager a() {
        return this.networkManager;
    }

    @Override
    public void disconnect(String s) {
        String leaveMessage = EnumChatFormat.YELLOW + this.player.getName() + " left the game.";
        PlayerKickEvent event = new PlayerKickEvent(MinecraftServer.getServer().server.getPlayer(this.player), s, leaveMessage);
        if (MinecraftServer.getServer().isRunning()) {
            MinecraftServer.getServer().server.getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            return;
        }

        s = event.getReason();
        entityBot.disconnect(s);
    }

    public void receivePacket(Packet packet) {
        ChannelHandlerContext context = this.networkManager.channel.pipeline().lastContext();

        try {
            NETWORK_MANAGER_RECEIVE_METHOD.invoke(this.networkManager, context, packet);
        } catch (Throwable throwable) {
            Stacktrace.print(throwable);
        }
    }

    public BotPacketBehaviour<?> getPacketBehaviour(Class<? extends Packet> packetClass) {
        if (PACKET_BEHAVIOURS.containsKey(packetClass)) {
            return PACKET_BEHAVIOURS.get(packetClass);
        }

        return null;
    }

    public void sendPacket(Packet packet) {
        if (packet == null) {
            return;
        }
        super.sendPacket(packet);

        BotPacketBehaviour packetBehaviour = this.getPacketBehaviour(packet.getClass());
        if (packetBehaviour != null) {
            packetBehaviour.handle(this.entityBot, this, packet);
        }
    }
}