package eu.girc.terra.proxy;

import eu.girc.terra.init.GIRCClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	@Override
	public void preinit() {
		super.preinit();
		MinecraftForge.EVENT_BUS.register(GIRCClient.class);
	}
	
}
