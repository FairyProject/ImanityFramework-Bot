package org.imanity.framework.bukkit.bot.v1_8_R3.connection;

import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.imanity.framework.bukkit.reflection.resolver.FieldResolver;
import org.imanity.framework.bukkit.reflection.wrapper.FieldWrapper;

import java.net.SocketAddress;

public class BotNetworkManager extends NetworkManager {

    private static FieldWrapper<SocketAddress> NETWORK_ADDRESS;

    static {
        try {
            NETWORK_ADDRESS = new FieldResolver(NetworkManager.class).resolveByFirstTypeWrapper(SocketAddress.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public BotNetworkManager(EnumProtocolDirection flag) {
        super(flag);
        this.channel = new BotChannel(null);
        NETWORK_ADDRESS.set(this, new SocketAddress() {
            private static final long serialVersionUID = 8207338859896320185L;
        });
    }

    @Override
    public boolean g() {
        return true;
    }
}