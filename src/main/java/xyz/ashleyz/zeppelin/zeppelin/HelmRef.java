package xyz.ashleyz.zeppelin.zeppelin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.ashleyz.zeppelin.blocks.StandableBlock;

import java.util.List;
import java.util.Timer;

public class HelmRef {
    public Zeppelin zeppelin;
    public boolean hasShownBlocks;
    public boolean hasFirstDone = false;
    public Timer timer;
    public List<BlockPos> awaiting;
    public World world;
    public List<StandableBlock> connectedBlocks;
    public List<BlockPos> alreadyAdded;
    public boolean readyToSpawn = false;
    public boolean enabled = false;
}
