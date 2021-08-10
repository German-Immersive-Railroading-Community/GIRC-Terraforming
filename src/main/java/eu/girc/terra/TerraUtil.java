package eu.girc.terra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;

import eu.girc.terra.item.TerraformItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class TerraUtil {

	public static enum FunctionType {
		LINEAR(1), PARABOLA(1);

		final int parameterCount;

		private FunctionType(final int parameterCount) {
			this.parameterCount = parameterCount;
		}
	}

	public static enum MergeFunctionType {
		ADD, SUBTRACT, RVSUBTRACT
	}

	private static double[] NBT_INFO = new double[128];

	public static HashMap<FunctionType, BiFunction<Integer, Integer, Byte>> DICTIONARY = Maps.newHashMap();
	public static HashMap<MergeFunctionType, BiFunction<Double, Double, Integer>> MERGE_DICTIONARY = Maps.newHashMap();
	static {
		Arrays.fill(NBT_INFO, 0);

		DICTIONARY.put(FunctionType.LINEAR, (st, x) -> (byte) (x * NBT_INFO[st]));
		DICTIONARY.put(FunctionType.PARABOLA, (st, x) -> (byte) (x * x * NBT_INFO[st]));

		MERGE_DICTIONARY.put(MergeFunctionType.ADD, (x, z) -> (int) (x + z));
		MERGE_DICTIONARY.put(MergeFunctionType.SUBTRACT, (x, z) -> (int) (x - z));
		MERGE_DICTIONARY.put(MergeFunctionType.RVSUBTRACT, (x, z) -> (int) (z - x));
	}

	public static final String XTYPE = "xtype";
	public static final String YTYPE = "ytype";
	public static final String XOFFTYPE = "xofftype";
	public static final String YOFFTYPE = "yofftype";
	public static final String XYMERGETYPE = "xymergetype";
	public static final String OLDMERGETYPE = "oldmergetype";
	public static final String XOFFSETTYPE = "xoffsettype";
	public static final String YOFFSETTYPE = "yoffsettype";

	public static final String ARGS = "ARGS";

	public static final void generatePatches(final NBTTagCompound compound,
			final BiConsumer<BlockPos, BlockPos> onpatch) {

		final NBTTagList xList = compound.getTagList(ARGS, 6);
		for (int i = 0; i < xList.tagCount(); i++) {
			NBT_INFO[i] = xList.getDoubleAt(i);
		}

		final FunctionType xtype = FunctionType.values()[compound.getInteger(XTYPE)];
		final BiFunction<Integer, Integer, Byte> xfunction = DICTIONARY.get(xtype);
		final FunctionType ztype = FunctionType.values()[compound.getInteger(YTYPE)];
		final BiFunction<Integer, Integer, Byte> zfunction = DICTIONARY.get(ztype);
		final FunctionType xofftype = FunctionType.values()[compound.getInteger(XTYPE)];
		final BiFunction<Integer, Integer, Byte> xofffunction = DICTIONARY.get(xofftype);
		final FunctionType zofftype = FunctionType.values()[compound.getInteger(YTYPE)];
		final BiFunction<Integer, Integer, Byte> zofffunction = DICTIONARY.get(zofftype);

		final MergeFunctionType xymergetype = MergeFunctionType.values()[compound.getInteger(XYMERGETYPE)];
		final BiFunction<Double, Double, Integer> xymergefunction = MERGE_DICTIONARY.get(xymergetype);
		final MergeFunctionType oldmergetype = MergeFunctionType.values()[compound.getInteger(OLDMERGETYPE)];
		final BiFunction<Double, Double, Integer> oldmergefunction = MERGE_DICTIONARY.get(oldmergetype);

		final MergeFunctionType xoffsettype = MergeFunctionType.values()[compound.getInteger(XOFFSETTYPE)];
		final BiFunction<Double, Double, Integer> xoffsetfunction = MERGE_DICTIONARY.get(xoffsettype);
		final MergeFunctionType yoffsettype = MergeFunctionType.values()[compound.getInteger(YOFFSETTYPE)];
		final BiFunction<Double, Double, Integer> yoffsetfunction = MERGE_DICTIONARY.get(yoffsettype);

		final BlockPos pos1 = NBTUtil.getPosFromTag(compound.getCompoundTag(TerraformItem.BLOCKPOS1));
		final BlockPos pos2 = NBTUtil.getPosFromTag(compound.getCompoundTag(TerraformItem.BLOCKPOS2));
		final BlockPos diff = pos2.subtract(pos1);
		final byte cheight = (byte) Math.max(pos1.getY(), pos2.getY());
		final int xM = diff.getX() > 0 ? 1 : -1;
		final int yM = diff.getY() > 0 ? -1 : 1;

		final byte[][] heightmap = new byte[Math.abs(diff.getX())][Math.abs(diff.getZ())];
		Arrays.stream(heightmap).forEach(arr -> Arrays.fill(arr, cheight));

		for (int i = 0; i < heightmap.length; i++) {
			final byte[] row = heightmap[i];
			final byte xinfo = xfunction.apply(0,
					xoffsetfunction.apply((double) i, (double) xofffunction.apply(64, i)));
			for (int j = 0; j < row.length; j++) {
				row[j] = (byte) (int) oldmergefunction.apply((double) row[j],
						(double) xymergefunction.apply((double) xinfo, (double) zfunction.apply(32,
								yoffsetfunction.apply((double) j, (double) zofffunction.apply(96, j)))));
			}
		}

		for (int i = 0; i < heightmap.length; i++) {
			final byte[] row = heightmap[i];
			for (int j = 0; j < row.length; j++) {
				final BlockPos npos1 = new BlockPos(i * xM + pos1.getX(), 0, j * yM + pos1.getZ());
				final BlockPos npos2 = npos1.add(0, Math.abs((int) row[j]), 0);
				onpatch.accept(npos1, npos2);
			}
		}
	}

}
