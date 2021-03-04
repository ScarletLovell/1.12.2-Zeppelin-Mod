package xyz.ashleyz.zeppelin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.ashleyz.zeppelin.util.events.ClientRenderEvent;
import xyz.ashleyz.zeppelin.zeppelin.HelmRef;
import xyz.ashleyz.zeppelin.zeppelin.Zeppelin;

import java.util.*;

public class HelmBlock extends BlockRotateable {
	private Map<UUID, BlockPos> controllers = new HashMap<>();
	public Map<BlockPos, HelmRef> referral = new HashMap<>();
	public HelmRef helmCl = new HelmRef();
	public HelmRef helmSv = new HelmRef();
	public HelmBlock(String name, Material mat) {
		super(name, mat);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setHardness(5.0F);
		setResistance(15.0F);
		setHarvestLevel("Axe", 2);
		setLightLevel(0.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	
	
	@SideOnly(Side.CLIENT)
	private void blapPlayer(EntityPlayer p) {
		HelmRef ref = referral.get(controllers.get(p.getUniqueID()));
		ref.awaiting.clear();
		//p.sendMessage(new TextComponentString("Can't add anymore then "+maxConnected+" blocks to Zeppelin!"));
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(hand == EnumHand.OFF_HAND)
			return false;
		HelmRef ref;
		if(worldIn.isRemote) {
			ref = helmCl;
		} else {
			ref = helmSv;
			p.sendMessage(new TextComponentString("Preparing Zeppelin..."));
		}
		ref.enabled = !ref.enabled;
		ref.zeppelin = new Zeppelin(p);
		ItemStack stack = p.getHeldItem(hand);
		ref.world = worldIn;
		ref.connectedBlocks = new ArrayList<>();
		ref.alreadyAdded = new ArrayList<>();
		ref.zeppelin.addOriginalBlock(pos, this, worldIn, ref);
		ref.zeppelin.controller = p;
		ref.awaiting = new ArrayList<BlockPos>() {{ add(pos); }};
		ClientRenderEvent.refList.add(ref);
		if(stack.getUnlocalizedName().equalsIgnoreCase("item.radio_item")) {
		
		} else {
		
		}
		return true;
	}
	
	public void privateTest() {
		World worldIn = null;
		EntityPlayer p = null;
		for(StandableBlock block : this.referral.get(this.controllers.get(p.getUniqueID())).connectedBlocks)
			worldIn.removeEntity(block);
		this.referral.remove(this.controllers.get(p.getUniqueID()));
		this.controllers.remove(p.getUniqueID());
		p.sendMessage(new TextComponentString("Brung Zeppelin back into reality!"));
	}
}
