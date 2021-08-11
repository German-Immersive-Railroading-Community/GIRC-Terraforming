package eu.girc.terra.init;

import com.google.common.base.Charsets;

import eu.girc.terra.TerraUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
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
		final ItemStack stack = mp.getHeldItemMainhand();
		NBTTagCompound comp = stack.getTagCompound();
		if(comp == null)
			comp = new NBTTagCompound();
		int lastVal = 0;
		while((lastVal = payBuf.readInt()) != 0xFFFFFFFF) {
			final String name = payBuf.readCharSequence(lastVal, Charsets.UTF_8).toString();
			comp.setInteger(name, payBuf.readInt());
		}
		final NBTTagList xList = new NBTTagList();
		for (int i = 0; i < 128; i++) {
			xList.appendTag(new NBTTagDouble(0));
		}
		while((lastVal = payBuf.readInt()) != 0xFFFFFFFF) {
			final double val = payBuf.readDouble();
			xList.set(lastVal, new NBTTagDouble(val));
		}
		comp.setTag(TerraUtil.ARGS, xList);
		stack.setTagCompound(comp);
	}
	
}
