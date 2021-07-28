package eu.girc.terra.proxy;

import eu.girc.terra.TerraformMain;
import eu.girc.terra.gui.GuiHandler;
import eu.girc.terra.init.GIRCItems;
import eu.girc.terra.init.GIRCNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public FMLEventChannel CHANNEL; 

	public void preinit() {
		MinecraftForge.EVENT_BUS.register(GIRCItems.class);
		
		CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(GIRCNetwork.CHANNEL_NAME);
		CHANNEL.register(new GIRCNetwork());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(TerraformMain.MODID, new GuiHandler());
	}
	
}
