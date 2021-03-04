package xyz.ashleyz.zeppelin.util;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import xyz.ashleyz.zeppelin.util.packets.KeybindPacket;

public class ModPacketRegistry {
    private static int packetId = 0;
    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    
    public ModPacketRegistry() {
    }
    
    public static void registerMessages() {
        INSTANCE.registerMessage(KeybindPacket.Handler.class, KeybindPacket.class, packetId++, Side.SERVER);
    }
}
