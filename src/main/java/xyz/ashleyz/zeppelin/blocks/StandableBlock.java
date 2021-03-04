package xyz.ashleyz.zeppelin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.zeppelin.HelmRef;

import javax.annotation.Nullable;

public class StandableBlock extends Entity
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private Block block;
    private BlockPos original;
    public IBlockState state;
    private HelmRef ref;
    private BlockPos pos;
    private boolean shouldDropItem = false;
    private boolean dontSetBlock;
    private boolean hurtEntities;
    private NBTTagCompound tileEntityData;
    
    private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);
    
    public StandableBlock(BlockPos pos, Block block, World world, HelmRef ref)
    {
        super(world);
        this.noClip = false;
        this.state = block.getDefaultState();
        this.ref = ref;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.pos = pos;
        float x = pos.getX()+0.5F;
        float y = pos.getY();
        float z = pos.getZ()+0.5F;
        this.block = block;
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(new BlockPos(this));
    }
    
    public void forceBehindOriginal() {
    
    }
    
    
    @Override
    public boolean canBeAttackedWithItem()
    {
        return false;
    }
    
    public void setOrigin(BlockPos p_184530_1_)
    {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }
    
    @Override
    public void applyEntityCollision(Entity entityIn)
    {
        if (entityIn instanceof EntityBoat)
        {
            if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY)
            {
                super.applyEntityCollision(entityIn);
            }
        }
        else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY)
        {
            super.applyEntityCollision(entityIn);
        }
    }
    
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }
    
    @Override
    protected void entityInit()
    {
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
    }
    
    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
    
    private int test = 0;
    @Override
    public void onUpdate()
    {
        if(this.isDead)
            return;
        Block block = this.block;
        if (this.state == null || this.block == null || this.state.getMaterial() == Material.AIR) {
            this.setDead();
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
    
            BlockPos blockpos = new BlockPos(this);
            
            if(!this.hasNoGravity())
                this.motionY -= 0.03999999910593033D;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            
            if (this.tileEntityData != null && block.hasTileEntity(block.getDefaultState())) {
                TileEntity tileentity = this.world.getTileEntity(blockpos);
                if (tileentity != null) {
                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
                    for (String s : this.tileEntityData.getKeySet()) {
                        NBTBase nbtbase = this.tileEntityData.getTag(s);
                        if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                            nbttagcompound.setTag(s, nbtbase.copy());
                        }
                    }
                    tileentity.readFromNBT(nbttagcompound);
                    tileentity.markDirty();
                }
            }
            this.doBlockCollisions();
        }
    }
    
    public void moveRelativeUp(float strafe, float up, float friction) {
        float f = strafe * strafe + up * up;
        
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt(f);
            
            if (f < 1.0F)
                f = 1.0F;
            
            f = friction / f;
            up = up * f;
            this.motionY += (double)up;
        }
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }
    
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }
    
    @Override
    public void fall(float distance, float damageMultiplier) {
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
        compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setBoolean("DropItem", this.shouldDropItem);
        
        if (this.tileEntityData != null)
        {
            compound.setTag("TileEntityData", this.tileEntityData);
        }
    }
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        int i = compound.getByte("Data") & 255;
        if (compound.hasKey("Block", 8)) {
            this.state = Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i);
        } else if (compound.hasKey("TileID", 99)) {
            this.state = Block.getBlockById(compound.getInteger("TileID")).getStateFromMeta(i);
        } else {
            this.state = Block.getBlockById(compound.getByte("Tile") & 255).getStateFromMeta(i);
        }
        
        if (compound.hasKey("DropItem", 99))
        {
            this.shouldDropItem = compound.getBoolean("DropItem");
        }
        
        if (compound.hasKey("TileEntityData", 10))
        {
            this.tileEntityData = compound.getCompoundTag("TileEntityData");
        }
    }
    
    public void setHurtEntities(boolean p_145806_1_)
    {
        this.hurtEntities = p_145806_1_;
    }
    
    public void addEntityCrashInfo(CrashReportCategory category)
    {
        super.addEntityCrashInfo(category);
        
        if (this.block != null)
        {
            Block block = this.block;
            category.addCrashSection("Immitating block ID", Block.getIdFromBlock(block));
            category.addCrashSection("Immitating block data", block.getMetaFromState(this.block.getDefaultState()));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public World getWorldObj() {
        return this.world;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }
    
    @Override
    public boolean hasNoGravity() {
        return true;
    }
    
    public Block getTrueBlock() {
        return this.block;
    }
    
    @Nullable
    public IBlockState getBlock() {
        return this.state;
    }
    
    @Override
    public boolean ignoreItemEntityData()
    {
        return true;
    }
}