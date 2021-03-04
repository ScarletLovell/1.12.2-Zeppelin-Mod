package xyz.ashleyz.zeppelin.util.tilt;

import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KeyAt {
    public KeyAt() {
        activeKeys = new ArrayList<>();
    }
    public boolean isKeyDown = false;
    public Function<KeyAt, Boolean> func;
    public List<KeyBinding> activeKeys;
}
