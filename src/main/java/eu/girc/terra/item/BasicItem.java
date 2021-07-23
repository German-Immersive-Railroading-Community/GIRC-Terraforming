package eu.girc.terra.item;

import eu.girc.terra.TerraformMain;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BasicItem extends Item {

	public BasicItem(final String name) {
		this.setRegistryName(TerraformMain.MODID, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}
	
}
