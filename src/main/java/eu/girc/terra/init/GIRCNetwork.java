package eu.girc.terra.init;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class GIRCNetwork {

	public static final String CHANNEL_NAME = "girc|terraforming";
	public static final byte TER_NBT_SET = 0;
	
	@SubscribeEvent
	public void onCustomPacket(final ServerCustomPacketEvent event) {
		FMLProxyPacket packet = event.getPacket();
		ByteBuf payBuf = packet.payload();
		EntityPlayerMP mp = ((NetHandlerPlayServer) event.getHandler()).player;
		World world = mp.world;
		MinecraftServer server = mp.getServer();
		byte id = payBuf.readByte();
		switch (id) {
		case TER_NBT_SET:
			readNBT(payBuf, mp);
			break;

		default:
			throw new IllegalArgumentException("Unknown packet name!");
		}
	}
	
	public static void readNBT(final ByteBuf payBuf, final EntityPlayerMP mp) {
		
	}
	
}
