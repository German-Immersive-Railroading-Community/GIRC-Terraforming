package eu.girc.terra.proxy;

import eu.girc.terra.init.GIRCItems;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

	public void preinit() {
		MinecraftForge.EVENT_BUS.register(GIRCItems.class);
	}
	
}
