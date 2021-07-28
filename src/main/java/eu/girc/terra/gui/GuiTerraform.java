package eu.girc.terra.gui;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import eu.girc.terra.TerraUtil.FunctionType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class GuiTerraform extends GuiScreen {

	public static final int TOP_STRING_OFFSET = 15;
	public static final float STRING_SCALE = 1.5f;
	public static final int STRING_COLOR = 4210752;
	public static final int LEFT_OFFSET = 20;
	public static final int SIGNALTYPE_FIXED_WIDTH = 150;
	public static final int SIGNALTYPE_INSET = 20;
	public static final int MAXIMUM_GUI_HEIGHT = 320;
	public static final int GUI_INSET = 40;
	public static final int SIGNAL_RENDER_WIDTH_AND_INSET = 180;
	public static final int TOP_OFFSET = GUI_INSET;
	public static final int SIGNAL_TYPE_ID = -100;
	public static final int SETTINGS_HEIGHT = 20;
	public static final int ELEMENT_SPACING = 10;
	public static final int BOTTOM_OFFSET = TOP_OFFSET;
	public static final int CHECK_BOX_HEIGHT = 10;
	public static final int DEFAULT_ID = 200;
	public static final int PAGE_SELECTION_ID = -890;
	public static final int TEXT_FIELD_ID = -200;

	public interface IIntegerable<T> {

		public T getObjFromID(int obj);

		public int count();
	}

	public static class EnumIntegerable<T extends Enum<T>> implements IIntegerable<T> {

		private Class<T> t;

		private EnumIntegerable(Class<T> t) {
			this.t = t;
		}

		@Override
		public T getObjFromID(int obj) {
			return t.getEnumConstants()[obj];
		}

		@Override
		public int count() {
			return t.getEnumConstants().length;
		}
		
		public static <T extends Enum<T>> IIntegerable<T> of(Class<T> t) {
			return new EnumIntegerable<T>(t);
		}

	}

	public interface ObjGetter<D> {
		D getObjFrom(int x);
	}

	public static class SizeIntegerables<T> implements IIntegerable<T> {

		private final int count;
		private final ObjGetter<T> getter;

		private SizeIntegerables(final int count, final ObjGetter<T> getter) {
			this.count = count;
			this.getter = getter;
		}

		@Override
		public T getObjFromID(int obj) {
			return this.getter.getObjFrom(obj);
		}

		@Override
		public int count() {
			return count;
		}

		public static <T> IIntegerable<T> of(final int count, final ObjGetter<T> get) {
			return new SizeIntegerables<T>(count, get);
		}

	}

	public class Setting {
		public final IIntegerable<?> iintg;
		public final String name;
		public final int defaultValue;
		public final Consumer<Integer> consumer;

		public Setting(final IIntegerable<?> iintg, final String name, final int defaultValue,
				final Consumer<Integer> consumer) {
			this.iintg = iintg;
			this.name = name;
			this.defaultValue = defaultValue;
			this.consumer = consumer;
		}
	}

	private int xSize;
	private int ySize;
	private int guiLeft;
	private int guiTop;
	private ArrayList<ArrayList<Object>> pageList = new ArrayList<>();
	private int indexCurrentlyUsed;

	public void update(final int value) {

	}

	private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
	public static final float DIM = 256.0f;

	public static void drawBack(GuiScreen gui, final int xLeft, final int xRight, final int yTop, final int yBottom) {
		gui.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

		gui.drawTexturedModalRect(xLeft, yTop, 0, 32, 4, 4);
		gui.drawTexturedModalRect(xLeft, yBottom, 0, 124, 4, 4);
		gui.drawTexturedModalRect(xRight, yTop, 24, 32, 4, 4);
		gui.drawTexturedModalRect(xRight, yBottom, 24, 124, 4, 4);

		drawScaledCustomSizeModalRect(xLeft + 4, yBottom, 4, 124, 1, 4, xRight - 4 - xLeft, 4, DIM, DIM);
		drawScaledCustomSizeModalRect(xLeft + 4, yTop, 4, 32, 1, 4, xRight - 4 - xLeft, 4, DIM, DIM);
		drawScaledCustomSizeModalRect(xLeft, yTop + 4, 0, 36, 4, 1, 4, yBottom - 4 - yTop, DIM, DIM);
		drawScaledCustomSizeModalRect(xRight, yTop + 4, 24, 36, 4, 1, 4, yBottom - 4 - yTop, DIM, DIM);

		drawRect(xLeft + 4, yTop + 4, xRight, yBottom, 0xFFC6C6C6);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBack(this, guiLeft, guiLeft + xSize, guiTop, guiTop + ySize);

		super.drawScreen(mouseX, mouseY, partialTicks);

	}

	@Override
	public void initGui() {
		this.xSize = SIGNALTYPE_FIXED_WIDTH + SIGNAL_RENDER_WIDTH_AND_INSET + SIGNALTYPE_INSET;
		this.ySize = Math.min(MAXIMUM_GUI_HEIGHT, this.height - GUI_INSET);
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		int yPos = this.guiTop + TOP_OFFSET;
		final int xPos = this.guiLeft + LEFT_OFFSET;
		int index = 0;
		pageList.clear();

		final Setting[] settingList = {
				new Setting(EnumIntegerable.of(FunctionType.class), "xtype", index, this::update) };

		pageList.add(Lists.newArrayList());
		yPos = this.guiTop + SETTINGS_HEIGHT + ELEMENT_SPACING + TOP_OFFSET;
		for (final Setting set : settingList) {
			if (set.iintg == null)
				continue;
			if (yPos >= (this.guiTop + this.ySize - BOTTOM_OFFSET)) {
				pageList.add(Lists.newArrayList());
				index++;
				yPos = this.guiTop + SETTINGS_HEIGHT + ELEMENT_SPACING + TOP_OFFSET;
			}
			final GuiEnumerableSetting rsModeSetting = new GuiEnumerableSetting(set.iintg, SIGNAL_TYPE_ID + 1, xPos,
					yPos, SIGNALTYPE_FIXED_WIDTH, set.name, set.defaultValue < set.iintg.count() ? set.defaultValue : 0,
					set.consumer);
			addButton(rsModeSetting);
			rsModeSetting.visible = index == indexCurrentlyUsed;
			pageList.get(index).add(rsModeSetting);
			yPos += SETTINGS_HEIGHT + ELEMENT_SPACING;
		}

		if (pageList.size() > 1) {
			final IIntegerable<String> sizeIn = SizeIntegerables.of(pageList.size(),
					idx -> (String) (idx + "/" + (pageList.size() - 1)));
			final GuiEnumerableSetting pageSelection = new GuiEnumerableSetting(sizeIn, PAGE_SELECTION_ID, 0,
					this.guiTop + this.ySize - BOTTOM_OFFSET + ELEMENT_SPACING, 0, "page", indexCurrentlyUsed, inp -> {
						pageList.get(indexCurrentlyUsed).forEach(visible(false));
						pageList.get(inp).forEach(visible(true));
						indexCurrentlyUsed = inp;
					}, false);
			pageSelection.setWidth(
					mc.fontRenderer.getStringWidth(pageSelection.displayString) + GuiEnumerableSetting.OFFSET * 2);
			pageSelection.x = this.guiLeft + ((SIGNALTYPE_FIXED_WIDTH - pageSelection.width) / 2)
					+ GuiEnumerableSetting.BUTTON_SIZE;
			pageSelection.update();
			addButton(pageSelection);
		}

	}

	public static Consumer<Object> visible(final boolean b) {
		return obj -> {
			if (obj instanceof GuiButton)
				((GuiButton) obj).visible = b;
			if (obj instanceof GuiTextField)
				((GuiTextField) obj).setVisible(b);
		};
	}

}
