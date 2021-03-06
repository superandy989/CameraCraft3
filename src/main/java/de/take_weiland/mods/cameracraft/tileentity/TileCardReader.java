package de.take_weiland.mods.cameracraft.tileentity;

import de.take_weiland.mods.cameracraft.api.PhotoStorageProvider;
import de.take_weiland.mods.cameracraft.api.cable.DataNetwork;
import de.take_weiland.mods.cameracraft.api.cable.NetworkEvent;
import de.take_weiland.mods.cameracraft.api.cable.NetworkNode;
import de.take_weiland.mods.cameracraft.api.cable.NetworkTile;
import de.take_weiland.mods.cameracraft.api.photo.PhotoStorage;
import de.take_weiland.mods.cameracraft.api.photo.PhotoStorageItem;
import de.take_weiland.mods.cameracraft.blocks.MachineType;
import de.take_weiland.mods.cameracraft.item.PhotoStorageType;
import de.take_weiland.mods.cameracraft.networking.NetworkNodeImpl;
import de.take_weiland.mods.commons.tileentity.TileEntityInventory;
import de.take_weiland.mods.commons.util.ItemStacks;
import de.take_weiland.mods.commons.util.Multitypes;
import net.minecraft.item.ItemStack;

public class TileCardReader extends TileEntityInventory<TileCardReader> implements NetworkTile, PhotoStorageProvider {

	public static final int NO_ACC = 0;
	public static final int READ_ACC = 1;
	public static final int WRITE_ACC = 2;
	
	private int access = NO_ACC;
	
	private NetworkNodeImpl node = new NetworkNodeImpl(this);
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	protected String unlocalizedName() {
		return Multitypes.fullName(MachineType.CARD_READER);
	}

	public int getAccessState() {
		return access;
	}

	public void setAccessState(int accessState) {
		this.access = accessState;
	}

	@Override
	public void updateEntity() {
		node.update();
		if (storage[0] != null) {
			access = READ_ACC;
		} else {
			access = NO_ACC;
		}
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		super.setInventorySlotContents(slot, item);
		if (slot == 0) {
			DataNetwork nw = node.getNetwork();
			if (nw != null) {
				nw.dispatch(new NetworkEvent(nw, NetworkEvent.Type.CUSTOM, node, PhotoStorageProvider.STORAGE_CHANGED_EVENT));
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return slot == 0 && ItemStacks.is(item, PhotoStorageType.MEMORY_CARD);
	}
	
	private PhotoStorage lastStorage;
	private ItemStack lastStorageItem;

	@Override
	public PhotoStorage getPhotoStorage() {
		if (isItemValidForSlot(0, storage[0])) {
			if (storage[0] == lastStorageItem) {
				return lastStorage;
			} else {
				lastStorageItem = storage[0];
				return (lastStorage = ((PhotoStorageItem) storage[0].getItem()).getPhotoStorage(storage[0]));
			}
		} else {
			return null;
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		node.shutdown();
	}

	@Override
	public String getNetworkName() {
		return (hasCustomName() ? getCustomName() : "CardReader") + " @ " + xCoord + ", " + yCoord + ", " + zCoord;
	}

	@Override
	public NetworkNode getNode() {
		return node;
	}
	

}
