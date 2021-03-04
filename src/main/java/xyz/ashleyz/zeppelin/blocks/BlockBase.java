package xyz.ashleyz.zeppelin.blocks;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import xyz.ashleyz.zeppelin.Main;
import xyz.ashleyz.zeppelin.init.ModBlocks;
import xyz.ashleyz.zeppelin.init.ModItems;
import xyz.ashleyz.zeppelin.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel
{
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public BlockBase(String name, Material material)
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	public static void Implement(BlockRotateable rotateable, final String name) {
		rotateable.setUnlocalizedName(name);
		rotateable.setRegistryName(name);
		rotateable.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		
		ModBlocks.BLOCKS.add(rotateable);
		ModItems.ITEMS.add(new ItemBlock(rotateable).setRegistryName(rotateable.getRegistryName()));
	}
	
	@Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
