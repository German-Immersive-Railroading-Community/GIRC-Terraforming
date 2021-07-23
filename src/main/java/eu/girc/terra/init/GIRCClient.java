package eu.girc.terra.init;

import eu.girc.terra.item.TerraformItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GIRCClient {
	
	@SubscribeEvent
	public static void renderOverlay(final RenderWorldLastEvent evt) {
		final EntityPlayerSP sp = Minecraft.getMinecraft().player;
		if(sp == null)
			return;
		final ItemStack stack = sp.getHeldItemMainhand();
		final Item item = stack.getItem();
		if(item == GIRCItems.TERRAFORM) {
			final NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null)
				return;
			if(nbt.hasKey(TerraformItem.BLOCKPOS1) && nbt.hasKey(TerraformItem.BLOCKPOS2)) {
				final BlockPos pos1 = NBTUtil.getPosFromTag(nbt.getCompoundTag(TerraformItem.BLOCKPOS1));
				final BlockPos pos2 = NBTUtil.getPosFromTag(nbt.getCompoundTag(TerraformItem.BLOCKPOS2));								
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.glLineWidth(2.0F);
	            GlStateManager.disableTexture2D();
	            GlStateManager.depthMask(false);

	            double part = evt.getPartialTicks();
	            
                double d1 = sp.lastTickPosX + (sp.posX - sp.lastTickPosX) * part;
                double d2 = sp.lastTickPosY + (sp.posY - sp.lastTickPosY) * part;
                double d3 = sp.lastTickPosZ + (sp.posZ - sp.lastTickPosZ) * part;
                final Vec3d nrmPos1 = new Vec3d(pos1.getX() - d1, pos1.getY() - d2, pos1.getZ() - d3);
                final Vec3d nrmPos2 = new Vec3d(pos2.getX() - d1, pos2.getY() - d2, pos2.getZ() - d3);
				RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(nrmPos1, nrmPos2.subtract(nrmPos1)) , 0, 1, 0, 1.0f);
 
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
	            GlStateManager.enableTexture2D();
			}
		}
	}
	
}
