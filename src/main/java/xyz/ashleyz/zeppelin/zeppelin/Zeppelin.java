package xyz.ashleyz.zeppelin.zeppelin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.blocks.StandableBlock;
import xyz.ashleyz.zeppelin.util.ZepReference;
import xyz.ashleyz.zeppelin.util.events.ClientRenderEvent;

import javax.annotation.Nullable;
import java.util.*;


public class Zeppelin {
    public static List<Zeppelin> all = new ArrayList<>();
    private List<ZeppelinBlock> blocks;
    public List<EntityPlayer> touching;
    public ZeppelinBlock originalBlock;
    public EntityPlayer controller;
    protected float lastFwd = 0;
    protected float lastUp = 0;
    private long cId;
    public Zeppelin(EntityPlayer p) {
        this.controller = p;
        this.cId = p.getUniqueID().getMostSignificantBits();
        ZepReference.push(this.cId, 0, 0, 0, false);
        this.originalBlock = null;
        this.blocks = new ArrayList<>();
        this.touching = new ArrayList<>();
        all.add(this);
    }
    
    public ZeppelinBlock getOriginalBlock() {
        return originalBlock;
    }
    
    private static List<Zeppelin> zeppelins = new ArrayList<>();
    @Nullable
    public static Zeppelin getZeppelinFrom(UUID uuid) {
        for(Zeppelin zeppelin : zeppelins) {
            if(zeppelin.controller.getUniqueID() == uuid)
                return zeppelin;
        }
        return null;
    }
    
    public void removeAll() {
        for(ZeppelinBlock block : blocks)
            this.originalBlock.world.removeEntity(block);
        this.touching = null;
        this.controller = null;
        this.lastFwd = this.lastUp = 0.0F;
        this.blocks = null;
    }
    
    public class ZeppelinBlock extends StandableBlock {
        private BlockPos relative;
        private Vec3d lastVec;
        public Zeppelin zeppelin;
        public ZeppelinBlock(Zeppelin zeppelin, BlockPos pos, Block block, World world, HelmRef ref) {
            super(pos, block, world, ref);
            this.zeppelin = zeppelin;
            ref.world.spawnEntity(this);
            if(block instanceof BlockFence) {
                BlockFence fence = (BlockFence) block;
                this.state = fence.getActualState(world.getBlockState(pos), world, pos);
            } else if(block instanceof BlockDoor) {
                BlockDoor door = (BlockDoor) block;
                this.state = door.getActualState(world.getBlockState(pos), world, pos);
            } else if(block instanceof BlockFenceGate) {
                BlockFenceGate gate = (BlockFenceGate) block;
                this.state = gate.getActualState(world.getBlockState(pos), world, pos);
            } else if(block instanceof BlockTrapDoor) {
                this.state = world.getBlockState(pos);
            } else {
                try {
                    EnumFacing facing = this.state.getValue(FACING);
                    Main.logger.info(facing);
                    switch(facing) {
                        case DOWN:
                            this.setRotation(90, 0);
                            break;
                        case WEST:
                            this.setRotation(90 + 90, 0);
                            break;
                        case EAST:
                            this.setRotation(90 + 90 + 90, 0);
                            break;
                        case SOUTH:
                            this.setRotation(90 + 90 + 90 + 90, 0);
                            break;
                        default:
                            this.setRotation(0, 0);
                            break;
                    }
                } catch(IllegalArgumentException ignored) { }
            }
            ref.world.updateEntity(this);
            this.lastVec = getVec3d();
            if(originalBlock != null) {
                BlockPos ori = new BlockPos(zeppelin.originalBlock.posX, zeppelin.originalBlock.posY, zeppelin.originalBlock.posZ);
                BlockPos th = new BlockPos(this);
                this.relative = new BlockPos(th.getX() - ori.getX(), th.getY() - ori.getY(), th.getZ() - ori.getZ());
            }
        }
    
        public void forceToOriginal() {
            if(this.relative == null || zeppelin.originalBlock == null)
                return;
            double oX = zeppelin.originalBlock.posX;
            double oY = zeppelin.originalBlock.posY;
            double oZ = zeppelin.originalBlock.posZ;
            float yw = zeppelin.originalBlock.rotationYaw/32;
            double toX = relative.getX()-yw;
            double toY = relative.getY();
            double toZ = relative.getZ()+yw;
            this.setPosition(oX+toX, oY+toY, oZ+toZ);
            this.rotationPitch = zeppelin.originalBlock.rotationPitch;
            this.rotationYaw = zeppelin.originalBlock.rotationYaw;
        }
        
        public Vec3d getVec3d() {
            return new Vec3d(this.motionX, this.motionY, this.motionZ);
        }
        
        public void rotateSlow(float yaw) {
            Vec3d vec3d = getVec3d();
            float dist = Float.valueOf(String.valueOf(vec3d.distanceTo(lastVec)));
            float go = dist + (yaw * 0.2F);
            this.lastVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
            if((this.rotationYaw+go) >= 359) {
                super.setRotation(-360, this.rotationPitch);
            } else if((this.rotationYaw+go) <= -359) {
                super.setRotation(360, this.rotationPitch);
            } else
                super.setRotation(this.rotationYaw+go, this.rotationPitch);
            //moveRelative(1 / 10, 0, 0, 0.02F);
        }
    
        @Override
        public void moveRelative(float strafe, float up, float forward, float friction) {
            super.moveRelative(strafe, up, forward, friction);
            this.lastVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
        }
        
        private boolean isOnBlock(EntityPlayer p, List<Entity> list) {
            for(Entity entity : list)
                if(entity instanceof EntityPlayer)
                    if(entity.getUniqueID() == p.getUniqueID())
                        return true;
            return false;
        }
    
        private int toStop = 0;
        private float goFwd = 0;
        private float toLft = 0;
        @Override
        public void onUpdate() {
            AxisAlignedBB bb = this.getEntityBoundingBox();
            bb = bb.offset(0, 0.06, 0);
            bb = bb.grow(-0.01, 0.05, -0.01);
            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, bb, EntitySelectors.getTeamCollisionPredicate(this));
            if (!list.isEmpty()) {
                for(EntityPlayer p : zeppelin.touching) {
                    if(!isOnBlock(p, list)) {
                        p.setAIMoveSpeed(0.1F);
                        zeppelin.touching.remove(p);
                    }
                }
                for(Entity entity : list) {
                    if(entity instanceof EntityPlayer) {
                        EntityPlayer p = (EntityPlayer) entity;
                        p.motionX = zeppelin.originalBlock.motionX;
                        p.posY = this.posY+0.98F;
                        this.world.updateEntityWithOptionalForce(this, false);
                        p.motionZ = zeppelin.originalBlock.motionZ;
                        p.limbSwing = p.prevLimbSwingAmount = p.limbSwingAmount = 0;
                        p.setAIMoveSpeed(0.25F);
                        if(!zeppelin.touching.contains(p))
                            zeppelin.touching.add(p);
                    }
                }
            }
            if(zeppelin.originalBlock == this) {
                super.onUpdate();
                if(!zeppelin.touching.isEmpty()) {
                    Vec3d vec3d = getVec3d();
                    float f = Float.valueOf(String.valueOf(vec3d.distanceTo(lastVec)));
                    ClientRenderEvent.toPush = new String[] {
                            "Velocity: "+f,
                            "X: "+Math.floor(posX),
                            "Y: "+Math.floor(posY),
                            "Z: "+Math.floor(posZ)
                    };
                } else
                    ClientRenderEvent.toPush = null;
                ZepReference ref = ZepReference.grab(zeppelin.cId);
                if(ref != null) {
                    if(ref.forceStop) {
                        toLft = goFwd = 0;
                        this.motionX = 0;
                        this.motionY = 0;
                        this.motionZ = 0;
                        ref.forceStop = false;
                    } else {
                        if(ref.fwdBck != 0) {
                            goFwd -= -0.02F;
                            if(ref.fwdBck > 0)
                                goFwd +=0.02F;
                        }
                        if(ref.upDown != 0)
                            this.motionY += ref.upDown/150;
                        if(ref.lftRgt != 0) {
                            toLft+=ref.lftRgt;
                        }
                        this.motionX = (double)(MathHelper.sin(-this.rotationYaw * 0.01F) * goFwd);
                        this.motionZ = (double)(MathHelper.cos(this.rotationYaw * 0.01F) * goFwd);
                        rotateSlow(toLft * 15);
                    }
                    ref.upDown = 0;
                    ref.lftRgt = 0;
                    ref.fwdBck = 0;
                }
                return;
            }
            forceToOriginal();
            super.onUpdate();
        }
    }
    public void moveBlocks(float y, float fwd) {
        fwd = fwd / 1000;
        lastUp = y;
        lastFwd = fwd;
    }
    public ZeppelinBlock addOriginalBlock(BlockPos pos, Block block, World world, HelmRef ref) {
        ZeppelinBlock zb = new ZeppelinBlock(this, pos, block, world, ref);
        originalBlock = zb;
        blocks.add(zb);
        return zb;
    }
    public ZeppelinBlock addBlock(BlockPos pos, Block block, World world, HelmRef ref) {
        ZeppelinBlock zb = new ZeppelinBlock(this, pos, block, world, ref);
        blocks.add(zb);
        return zb;
    }
}
