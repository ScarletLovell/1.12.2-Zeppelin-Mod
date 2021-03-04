package xyz.ashleyz.zeppelin.util;

import xyz.ashleyz.zeppelin.util.events.ClientRenderEvent;

import java.util.HashMap;
import java.util.Map;

public class ZepReference {
    private static Map<Long, ZepReference> zepMap = new HashMap<>();
    public static void push(Long id, int fwdBck, int lftRgt, int upDown, boolean forceStop) {
        ZepReference ref;
        if(zepMap.containsKey(id)) {
            ref = zepMap.get(id);
            ref.fwdBck = fwdBck;
            ref.lftRgt = lftRgt;
            ref.upDown = upDown;
            ref.forceStop = forceStop;
            zepMap.replace(id, ref);
        } else {
            ref = new ZepReference();
            ref.fwdBck = fwdBck;
            ref.lftRgt = lftRgt;
            ref.upDown = upDown;
            ref.forceStop = forceStop;
            zepMap.put(id, ref);
        }
    }
    public static void eatMessage(float velocity, double x, double y, double z) {
        ClientRenderEvent.toPush = new String[]{
                "Velocity: " + velocity,
                "X: " + Math.floor(x),
                "Y: " + Math.floor(y),
                "Z: " + Math.floor(z)
        };
    }
    public static ZepReference grab(Long id) {
        if(zepMap.containsKey(id))
            return zepMap.get(id);
        return null;
    }
    public int fwdBck = 0;
    public int lftRgt = 0;
    public int upDown = 0;
    public boolean forceStop = false;
}
