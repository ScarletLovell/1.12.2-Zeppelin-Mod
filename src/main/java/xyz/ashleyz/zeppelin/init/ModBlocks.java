package xyz.ashleyz.zeppelin.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import xyz.ashleyz.zeppelin.blocks.HelmBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import xyz.ashleyz.zeppelin.blocks.StandableBlock;
import xyz.ashleyz.zeppelin.blocks.StandableBlockRender;

public class ModBlocks {
	public static void setupBlocks() {
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		manager.entityRenderMap.put(StandableBlock.class, new StandableBlockRender(manager));
	}
	public static final List<Block> BLOCKS = new ArrayList<>();
	
	public static final Block helm_block
			= new HelmBlock("airshipcontroller_block", Material.WOOD);
	
}
