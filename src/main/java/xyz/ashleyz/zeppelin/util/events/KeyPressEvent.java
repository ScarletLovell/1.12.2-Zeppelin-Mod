package xyz.ashleyz.zeppelin.util.events;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.util.tilt.KeyAt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KeyPressEvent {
    public static List<KeyAt> keyBindings;
    private static List<KeyAt> keysNotPressed;
    public KeyPressEvent() {
        keyBindings = new ArrayList<>();
        keysNotPressed = new ArrayList<>();
    }
    
    public static void AddKeybind(String desc, String category, Function<KeyAt, Boolean> func, int... key) {
        if(key.length < 1) {
            Main.logger.warn("Key from " + category + ", " + desc + " is NULL.");
            return;
        }
        KeyAt at = new KeyAt();
        for(int Key : key) {
            at.func = func;
            KeyBinding bind = new KeyBinding(desc, Key, category);
            at.activeKeys.add(bind);
            ClientRegistry.registerKeyBinding(bind);
        }
        keyBindings.add(at);
    }
    
    public static void AddKeysNotPressed(String desc, String category, Function<KeyAt, Boolean> func, int... key) {
        if(key.length < 1) {
            Main.logger.warn("Key from " + category + ", " + desc + " is NULL.");
            return;
        }
        KeyAt at = new KeyAt();
        for(int Key : key) {
            at.func = func;
            KeyBinding bind = new KeyBinding(desc, Key, category);
            at.activeKeys.add(bind);
            ClientRegistry.registerKeyBinding(bind);
        }
        keysNotPressed.add(at);
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void onEvent(InputEvent.KeyInputEvent event) {
        if(event.isCanceled())
            return;
        if(keyBindings.size() > 0) {
            for(KeyAt at : keyBindings) {
                int isDown = 0;
                for(KeyBinding bind : at.activeKeys) {
                    if(bind.isKeyDown())
                        isDown++;
                }
                if(isDown == at.activeKeys.size()) {
                    if(!at.func.apply(at))
                        event.setCanceled(true);
                }
            }
        }
        if(keysNotPressed.size() > 0) {
            for(KeyAt at : keysNotPressed) {
                int isNotDown = 0;
                for(KeyBinding bind : at.activeKeys) {
                    if(!bind.isKeyDown())
                        isNotDown++;
                }
                if(isNotDown == at.activeKeys.size()) {
                    if(!at.func.apply(at))
                        event.setCanceled(true);
                }
            }
        }
    }
}
