package de.take_weiland.mods.cameracraft.tileentity;

import java.util.List;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import de.take_weiland.mods.cameracraft.blocks.CCBlock;
import de.take_weiland.mods.cameracraft.blocks.MachineType;
import de.take_weiland.mods.commons.templates.TileEntityInventory;
import de.take_weiland.mods.commons.util.Blocks;
import de.take_weiland.mods.commons.util.ItemStacks;
import de.take_weiland.mods.commons.util.Sides;

public class TileItemMutator extends TileEntityInventory implements ISidedInventory {

	private short transmuteTime = 0;
	private boolean enabled = true;
	private int selectedResult = 0;
	
	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	protected String getDefaultName() {
		return Blocks.getUnlocalizedName(CCBlock.machines, MachineType.ITEM_MUTATOR);
	}
	
	@Override
	public void updateEntity() {
		if (transmuteTime > 0) {
			transmuteTime--;
		}
		if (Sides.logical(this).isServer()) {
			if (transmuteTime == -1 && canTransmute()) {
				startTransmuting();
			} else if (transmuteTime == 0) {
				transmute();
				transmuteTime = -1;
			} else if (transmuteTime > 0 && !hasSourceItem()) {
				transmuteTime = 0;
			}
		}
	}
	
	private void startTransmuting() {
		transmuteTime = 200;
	}
	
	private void transmute() {
		if (hasSelectedResult() && hasSourceItem()) {
			List<ItemStack> result = getTransmutingResult();
			ItemStack selected;
			if (result != null && ItemStacks.canMergeFully((selected = result.get(selectedResult)), storage[1])) {
				decrStackSize(0, 1);
				storage[1] = ItemStacks.merge(selected.copy(), storage[1], true);
			}
		}
	}

	private static List<ItemStack> getTransmutingResult(ItemStack item) {
		int oreId = OreDictionary.getOreID(item);
		if (oreId < 0) {
			return null;
		} else {
			List<ItemStack> similarOres = OreDictionary.getOres(Integer.valueOf(oreId));
			return similarOres.isEmpty() ? null : similarOres;
		}
	}
	
	public List<ItemStack> getTransmutingResult() {
		return getTransmutingResult(storage[0]);
	}

	private boolean hasSourceItem() {
		return storage[0] != null;
	}
	
	private boolean hasSelectedResult() {
		return selectedResult >= 0;
	}
	
	private boolean canTransmute() {
		List<ItemStack> temp;
		return hasSelectedResult() && hasSourceItem() && (temp = getTransmutingResult()) != null && ItemStacks.canMergeFully(temp.get(selectedResult), storage[1]);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		nbt.setShort("transmuteTime", transmuteTime);
		nbt.setBoolean("enabled", enabled);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		transmuteTime = nbt.getShort("transmuteTime");
		enabled = nbt.hasKey("enabled") ? nbt.getBoolean("enabled") : true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == 0) {
			return OreDictionary.getOreID(item) >= 0;
		}
		return false;
	}

	private static final int[] EXTRACT_SLOTS = new int[] { 1 };
	private static final int[] INSERT_SLOTS = new int[] { 0 };
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) {
			return EXTRACT_SLOTS;
		} else {
			return INSERT_SLOTS;
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return true;
	}

	public short getTransmuteTime() {
		return transmuteTime;
	}

	public void setTransmuteTime(short transmuteTime) {
		if (Sides.logical(this).isClient()) {
			this.transmuteTime = transmuteTime;
		}
	}

}