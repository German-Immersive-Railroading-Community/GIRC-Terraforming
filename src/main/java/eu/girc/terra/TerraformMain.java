package eu.girc.terra;

import org.apache.logging.log4j.Logger;

import eu.girc.terra.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TerraformMain.MODID)
public class TerraformMain
{
    public static final String MODID = "gircterraform";

    public static Logger logger;

    @SidedProxy(clientSide = "eu.girc.terra.proxy.ClientProxy", serverSide = "eu.girc.terra.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preinit();
    }

}
