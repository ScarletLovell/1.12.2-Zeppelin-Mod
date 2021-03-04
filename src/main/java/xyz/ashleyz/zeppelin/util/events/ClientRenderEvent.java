package xyz.ashleyz.zeppelin.util.events;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.zeppelin.HelmRef;
import xyz.ashleyz.zeppelin.zeppelin.Zeppelin;

import java.util.ArrayList;
import java.util.List;

public class ClientRenderEvent {
    public static String[] toPush = null;
    public static List<HelmRef> refList = new ArrayList<>();
    private int toStop = 0;
    
    int maxConnected = 10240;
    private List<Block> disallowed = new ArrayList<Block>() {
        {
            add(Blocks.AIR);
            add(Blocks.WATER);
            add(Blocks.FLOWING_WATER);
            add(Blocks.LAVA);
            add(Blocks.FLOWING_LAVA);
            add(Blocks.TALLGRASS);
            add(Blocks.GRASS);
        }
    };
    
    @SubscribeEvent
    public void createEntities(TickEvent.RenderTickEvent e) {
        if(e.phase != TickEvent.Phase.END)
            return;
        for(int i=0;i < refList.size();i++) {
            HelmRef ref = refList.get(i);
            World world = ref.world;
            if(ref.awaiting.size() < 1) {
                if(ref.alreadyAdded.size() < 1) {
                    refList.remove(ref);
                    continue;
                }
                if(world.isRemote && !ref.hasShownBlocks) {
                    ref.zeppelin.controller.sendMessage(new TextComponentString("Adding " + (ref.connectedBlocks.size()+1) + " blocks to the Zeppelin"));
                    ref.hasShownBlocks = true;
                }
                for(int o=0;o < ref.alreadyAdded.size();o++) {
                    BlockPos pos = ref.alreadyAdded.get(o);
                    if(world.isRemote) {
                        //ref.zeppelin.controller.sendMessage(new TextComponentString("Found " + (ref.awaiting.size()+1) + " blocks to add"));
                        ref.zeppelin.addBlock(pos, world.getBlockState(pos).getBlock(), world, ref);
                    } else {
                        world.setBlockToAir(pos);
                    }
                }
                ref.alreadyAdded.clear();
                continue;
            }
            List<BlockPos> pass = new ArrayList<>();
            pass.addAll(ref.awaiting);
            ref.awaiting.clear();
            for(BlockPos pos : pass) {
                if(disallowed.contains(world.getBlockState(pos).getBlock()) || ref.alreadyAdded.contains(pos))
                    continue;
                for(EnumFacing face : EnumFacing.values()) {
                    BlockPos newPos = pos.offset(face);
                    if(disallowed.contains(world.getBlockState(newPos).getBlock()) || ref.alreadyAdded.contains(newPos))
                        continue;
                    if(ref.alreadyAdded.size() > maxConnected) {
                        ref.awaiting.clear();
                        break;
                    }
                    ref.awaiting.add(newPos);
                }
                if(ref.hasFirstDone)
                    ref.alreadyAdded.add(pos);
                else
                    ref.hasFirstDone = true;
                if(ref.alreadyAdded.size() > maxConnected) {
                    ref.awaiting.clear();
                    break;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.EntityInteract e) {
        if(!(e.getEntity() instanceof EntityPlayer))
            return;
        if(e.getHand() != EnumHand.MAIN_HAND)
            return;
        EntityPlayer p = (EntityPlayer) e.getEntity();
        Entity target = e.getTarget();
        if(target instanceof Zeppelin.ZeppelinBlock) {
            Zeppelin.ZeppelinBlock block = ((Zeppelin.ZeppelinBlock) target);
            if(!block.zeppelin.originalBlock.equals(block))
                return;
            block.zeppelin.removeAll();
            p.sendMessage(new TextComponentString("Removing Zeppelin from world!"));
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onEvent(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.TEXT)
            return;
        if(toPush != null) {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen != null)
                return;
            int down = 0;
            for(String out : toPush) {
                down+=10;
                mc.fontRenderer.drawString(out, 10, mc.displayHeight - 80 + down, 0xFFFFFFF);
            }
            if(toStop > 50)
                toStop = 0;
            else
                toStop++;
        } else {
            if(toStop != 0)
                toStop = 0;
        }
    }
}
