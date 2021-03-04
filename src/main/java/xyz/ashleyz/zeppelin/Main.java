package xyz.ashleyz.zeppelin;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import xyz.ashleyz.zeppelin.blocks.StandableBlock;
import xyz.ashleyz.zeppelin.blocks.StandableBlockRender;
import xyz.ashleyz.zeppelin.init.ModRecipes;
import xyz.ashleyz.zeppelin.proxy.CommonProxy;
import xyz.ashleyz.zeppelin.util.ModPacketRegistry;
import xyz.ashleyz.zeppelin.util.Reference;
import xyz.ashleyz.zeppelin.util.ZepReference;
import xyz.ashleyz.zeppelin.util.events.ClientRenderEvent;
import xyz.ashleyz.zeppelin.util.events.OnTickEvent;
import xyz.ashleyz.zeppelin.util.events.KeyPressEvent;
import xyz.ashleyz.zeppelin.util.packets.KeybindPacket;

@Mod(
		modid = Reference.MOD_ID,
		name = Reference.NAME,
		version = Reference.VERSION,
		dependencies = Reference.DEPEND
)
public class Main {
	@Instance
	public static Main instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		MinecraftForge.EVENT_BUS.register(new OnTickEvent());
		MinecraftForge.EVENT_BUS.register(new KeyPressEvent());
		MinecraftForge.EVENT_BUS.register(new ClientRenderEvent());
		if(event.getSide().isClient()) {
			RenderingRegistry.registerEntityRenderingHandler(StandableBlock.class, StandableBlockRender::new);
			KeyPressEvent.AddKeybind("Stop", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(0, 0, 0, l, true));
				return true;
			}, Keyboard.KEY_NUMPAD5);
			KeyPressEvent.AddKeybind("Up", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(0, 0, 1, l, false));
				return true;
			}, Keyboard.KEY_ADD);
			KeyPressEvent.AddKeybind("Down", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(1, 0, -1, l, false));
				return true;
			}, Keyboard.KEY_SUBTRACT);
			KeyPressEvent.AddKeybind("Forward", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(1, 0, 0, l, false));
				return true;
			}, Keyboard.KEY_NUMPAD8);
			KeyPressEvent.AddKeybind("Turn Left", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(0, -1, 0, l, false));
				return true;
			}, Keyboard.KEY_NUMPAD4);
			KeyPressEvent.AddKeybind("Turn Right", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(0, 1, 0, l, false));
				return true;
			}, Keyboard.KEY_NUMPAD6);
			KeyPressEvent.AddKeybind("Backwards", Reference.NAME, (active) -> {
				long l = Minecraft.getMinecraft().player.getUniqueID().getMostSignificantBits();
				ModPacketRegistry.INSTANCE.sendToServer(new KeybindPacket(-1, 0, 0, l, false));
				return true;
			}, Keyboard.KEY_NUMPAD2);
		}
		ModPacketRegistry.registerMessages();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event) {
		ModRecipes.init();
	}
	
	@EventHandler
	public static void Postinit(FMLPostInitializationEvent event) {
		logger.info(Reference.NAME + " Loaded!");
	}

}
