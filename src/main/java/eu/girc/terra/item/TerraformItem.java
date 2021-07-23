package eu.girc.terra.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TerraformItem extends BasicItem {

	public TerraformItem() {
		super("terraformitem");
	}

	public static final String BLOCKPOS1 = "bpos1";
	public static final String BLOCKPOS2 = "bpos2";

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		this.onItemUse(playerIn, worldIn, null, handIn, null, 0,0,0);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand handIn,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(pos == null)
			return EnumActionResult.PASS;
		final ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		if(playerIn.isSneaking()) {
			nbt.removeTag(BLOCKPOS1);
			nbt.removeTag(BLOCKPOS2);
			return EnumActionResult.SUCCESS;
		}
		if(nbt.hasKey(BLOCKPOS1)) {
			if(nbt.hasKey(BLOCKPOS2))
				return EnumActionResult.FAIL;
			nbt.setTag(BLOCKPOS2, NBTUtil.createPosTag(pos));
		}
		nbt.setTag(BLOCKPOS1, NBTUtil.createPosTag(pos));
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
