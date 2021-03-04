package xyz.ashleyz.zeppelin.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class StandableBlockRender extends Render<StandableBlock> {
    public StandableBlockRender(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.5F;
    }
    
    protected void applyRotations(StandableBlock entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
        float f = (float)(entityLiving.world.getTotalWorldTime()) + partialTicks;
        
        if (f < 5.0F) {
            GlStateManager.rotate(MathHelper.sin(f / 1.5F * (float)Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
        }
    }
    
    @Override
    public void doRender(StandableBlock entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        BlockPos blockpos = new BlockPos(entity);
        IBlockState iblockstate = entity.state;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-entityYaw, 0, 1, 0);
        GlStateManager.translate(-blockpos.getX()-0.5F, -blockpos.getY(), -blockpos.getZ()-0.5F);
        blockrendererdispatcher.getBlockModelRenderer().renderModel(entity.world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos, bufferbuilder, false, MathHelper.getPositionRandom(new BlockPos(entity)));
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
    
    protected float handleRotationFloat(StandableBlock livingBase, float partialTicks) {
        return (float)livingBase.ticksExisted + partialTicks;
    }
    
    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(StandableBlock entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
