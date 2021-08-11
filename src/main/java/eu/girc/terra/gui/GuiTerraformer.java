package eu.girc.terra.gui;

import static eu.girc.terra.TerraUtil.OLDMERGETYPE;
import static eu.girc.terra.TerraUtil.XOFFSETTYPE;
import static eu.girc.terra.TerraUtil.XOFFTYPE;
import static eu.girc.terra.TerraUtil.XTYPE;
import static eu.girc.terra.TerraUtil.XYMERGETYPE;
import static eu.girc.terra.TerraUtil.YOFFSETTYPE;
import static eu.girc.terra.TerraUtil.YOFFTYPE;
import static eu.girc.terra.TerraUtil.YTYPE;
import static eu.girc.terra.TerraUtil.ARGS;

import java.util.function.Consumer;

import com.google.common.base.Charsets;

import eu.gir.girsignals.guis.guilib.DrawUtil.NamedEnumIntegerable;
import eu.gir.girsignals.guis.guilib.GuiBase;
import eu.gir.girsignals.guis.guilib.GuiElements.GuiDoubleSelect;
import eu.gir.girsignals.guis.guilib.GuiElements.GuiEnumerableSetting;
import eu.girc.terra.TerraUtil.FunctionType;
import eu.girc.terra.TerraUtil.MergeFunctionType;
import eu.girc.terra.TerraformMain;
import eu.girc.terra.init.GIRCNetwork;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class GuiTerraformer extends GuiBase {

	private NBTTagCompound compound;

	public GuiTerraformer(final ItemStack stack) {
		compound = stack.getTagCompound();
		if (compound == null)
			compound = new NBTTagCompound();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GuiEnumerableSetting of(String name, Class<?> t, Consumer<Integer> consumer) {
		return new GuiEnumerableSetting(new NamedEnumIntegerable(name, t), compound.getInteger(name), in -> {
			compound.setInteger(name, in);
			consumer.accept(in);
		});
	}

	public GuiDoubleSelect of(String dfname, int doubleindex, int offset, Consumer<Double> consumer) {
		NBTTagList list = compound.getTagList(ARGS, 6);
		GuiDoubleSelect select = new GuiDoubleSelect(dfname + " - " + doubleindex,
				list.getDoubleAt(doubleindex + offset), consumer);
		select.value = doubleindex + offset;
		return select;
	}

	@Override
	public void initButtons() {
		synchronized (buttonList) {
			super.initButtons();

			GuiEnumerableSetting ftypex = addButton(of(XTYPE, FunctionType.class, in -> initButtons()));
			GuiEnumerableSetting ftypey = addButton(of(YTYPE, FunctionType.class, in -> initButtons()));
			GuiEnumerableSetting ftypexo = addButton(of(XOFFTYPE, FunctionType.class, in -> initButtons()));
			GuiEnumerableSetting ftypeyo = addButton(of(YOFFTYPE, FunctionType.class, in -> initButtons()));

			addButton(of(XYMERGETYPE, MergeFunctionType.class, in -> {
			}));
			addButton(of(OLDMERGETYPE, MergeFunctionType.class, in -> {
			}));
			addButton(of(XOFFSETTYPE, MergeFunctionType.class, in -> {
			}));
			addButton(of(YOFFSETTYPE, MergeFunctionType.class, in -> {
			}));

			final int paramCountX = ((FunctionType) ftypex.property.getObjFromID(ftypex.getValue())).parameterCount;
			for (int i = 0; i < paramCountX; i++)
				addButton(of(I18n.format("paramx"), i, 0, in -> {
				}));

			final int paramCountY = ((FunctionType) ftypey.property.getObjFromID(ftypey.getValue())).parameterCount;
			for (int i = 0; i < paramCountY; i++)
				addButton(of(I18n.format("paramy"), i, 32, in -> {
				}));

			final int paramCountXo = ((FunctionType) ftypexo.property.getObjFromID(ftypexo.getValue())).parameterCount;
			for (int i = 0; i < paramCountXo; i++)
				addButton(of(I18n.format("paramxo"), i, 64, in -> {
				}));

			final int paramCountYo = ((FunctionType) ftypeyo.property.getObjFromID(ftypeyo.getValue())).parameterCount;
			for (int i = 0; i < paramCountYo; i++)
				addButton(of(I18n.format("paramyo"), i, 96, in -> {
				}));
			initGui();
		}
	}

	@Override
	public void onGuiClosed() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte(GIRCNetwork.TER_NBT_SET);
		for (GuiButton button : buttonList) {
			if (button instanceof GuiDoubleSelect)
				continue;
			if (button instanceof GuiEnumerableSetting) {
				final GuiEnumerableSetting setting = (GuiEnumerableSetting) button;
				final String name = setting.property.getName();
				final int len = name.length();
				buffer.writeInt(len);
				buffer.writeCharSequence(name, Charsets.UTF_8);
				buffer.writeInt(setting.getValue());
			}
		}
		buffer.writeInt(0xFFFFFFFF);
		for (GuiButton button : buttonList) {
			if (button instanceof GuiDoubleSelect) {
				final GuiDoubleSelect setting = (GuiDoubleSelect) button;
				System.out.println(setting.getText());
				System.out.println(setting.getValue());

				buffer.writeInt(setting.getValue());
				buffer.writeDouble(setting.select);
			}
		}
		buffer.writeInt(0xFFFFFFFF);
		CPacketCustomPayload payload = new CPacketCustomPayload(GIRCNetwork.CHANNEL_NAME, new PacketBuffer(buffer));
		FMLProxyPacket packet = new FMLProxyPacket(payload);
		TerraformMain.proxy.CHANNEL.sendToServer(packet);
	}

	@Override
	public String getTitle() {
		return "Terraformer";
	}

}
