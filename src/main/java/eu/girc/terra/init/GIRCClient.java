package eu.girc.terra.init;

import eu.girc.terra.TerraUtil;
import eu.girc.terra.item.TerraformItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GIRCClient {

	private static double d1;
	private static double d2;
	private static double d3;

	public static void render(final BlockPos pos1) {
		RenderGlobal.drawSelectionBoundingBox(Block.FULL_BLOCK_AABB.offset(((double) pos1.getX()) - d1,
				((double) pos1.getY()) - d2, ((double) pos1.getZ()) - d3), 0, 1, 0, 1);
	}

	public static void renderArea(final BlockPos pos1, final BlockPos pos2) {
		if(pos1.equals(pos2)) {
			render(pos1);
			return;
		}
		RenderGlobal
				.drawSelectionBoundingBox(
						new AxisAlignedBB(BlockPos.ORIGIN, pos2.subtract(pos1).add(1, 2, 1)).offset(
								((double) pos1.getX()) - d1, ((double) pos1.getY()) - d2, ((double) pos1.getZ()) - d3),
						1, 0, 0, 1);
	}

	@SubscribeEvent
	public static void regModel(final ModelBakeEvent evt) {
		ModelLoader.setCustomModelResourceLocation(GIRCItems.TERRAFORM, 0,
				new ModelResourceLocation(GIRCItems.TERRAFORM.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public static void renderOverlay(final RenderWorldLastEvent evt) {
		final EntityPlayerSP sp = Minecraft.getMinecraft().player;
		if (sp == null)
			return;
		final ItemStack stack = sp.getHeldItemMainhand();
		final Item item = stack.getItem();
		if (item == GIRCItems.TERRAFORM) {
			final NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null)
				return;

            final double part = evt.getPartialTicks();
            d1 = sp.lastTickPosX + (sp.posX - sp.lastTickPosX) * part;
        	d2 = sp.lastTickPosY + (sp.posY - sp.lastTickPosY) * part;
        	d3 = sp.lastTickPosZ + (sp.posZ - sp.lastTickPosZ) * part;
			if (nbt.hasKey(TerraformItem.BLOCKPOS1) && nbt.hasKey(TerraformItem.BLOCKPOS2)) {
				GlStateManager.disableTexture2D();
				{
					TerraUtil.generatePatches(nbt, GIRCClient::renderArea);
				}
				GlStateManager.enableTexture2D();

			}
		}
	}

}
