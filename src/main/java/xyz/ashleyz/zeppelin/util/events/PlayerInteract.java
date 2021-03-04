package xyz.ashleyz.zeppelin.util.events;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.blocks.StandableBlock;

public class PlayerInteract {
    @SubscribeEvent
    public void OnPlayerInteract(PlayerInteractEvent.EntityInteract e) {
        if(e.getTarget() instanceof StandableBlock) {
            StandableBlock block = (StandableBlock) e.getTarget();
            Main.logger.info(block.getBlock().getBlock().getUnlocalizedName());
        }
    }
}
