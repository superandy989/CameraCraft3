package de.take_weiland.mods.cameracraft.photo;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import de.take_weiland.mods.cameracraft.api.camera.PhotoStorage;
import de.take_weiland.mods.commons.util.ItemStacks;
import de.take_weiland.mods.commons.util.NBT;

public final class PhotoStorages {

	private static final String NBT_KEY = "cameracraft.photos";

	private PhotoStorages() { }
	
	public static int fastSize(ItemStack stack) {
		return ItemStacks.getNbt(stack).getTagList(NBT_KEY).tagCount();
	}
	
	public static PhotoStorage withCapacity(int cap, boolean sealed, ItemStack stack) {
		return withCapacity(cap, sealed, NBT.getOrCreateList(ItemStacks.getNbt(stack), (NBT_KEY)));
	}
	
	public static PhotoStorage withCapacity(final int cap, boolean sealed, final NBTTagList nbt) {
		return new AbstractPhotoStorage(sealed) {
			
			@Override
			public int size() {
				return nbt.tagCount();
			}
			
			@Override
			public int capacity() {
				return cap;
			}
			
			@Override
			public void remove(int index) {
				checkNotSealed();
				nbt.removeTag(index);
				onChange();
			}

			@Override
			protected String getImpl(int index) {
				return ((NBTTagString)nbt.tagAt(index)).data;
			}

			@Override
			protected void storeImpl(String photoId) {
				nbt.appendTag(new NBTTagString("", photoId));
			}
		};
	}
	
}