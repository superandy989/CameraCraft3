package de.take_weiland.mods.cameracraft.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.take_weiland.mods.cameracraft.CameraCraft;
import de.take_weiland.mods.cameracraft.api.photo.PhotoItem;
import de.take_weiland.mods.cameracraft.api.photo.PhotoStorage;
import de.take_weiland.mods.cameracraft.photo.AbstractPhotoStorage;
import de.take_weiland.mods.cameracraft.photo.PhotoManager;
import de.take_weiland.mods.commons.util.ItemStacks;
import de.take_weiland.mods.commons.util.Sides;

public class ItemPhoto extends CCItemMultitype<PhotoType> implements PhotoItem {

	public static final String NBT_KEY = "cameracraft.photoId";
	private static final String NBT_NAME_KEY = "cameracraft.photoname";

	public ItemPhoto(int defaultId) {
		super("photo", defaultId);
		setMaxStackSize(1);
	}

	@Override
	public String getItemDisplayName(ItemStack stack) {
		if (isNamed(stack)) {
			return getNameImpl(stack);
		}
		return super.getItemDisplayName(stack);
	}

	@Override
	protected List<ItemStack> provideSubtypes() {
		return ImmutableList.of();
	}

	@Override
	public PhotoType[] getTypes() {
		return PhotoType.VALUES;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (Sides.logical(world).isClient() && stack.hasTagCompound()) {
			boolean isNamed = isNamed(stack);
			CameraCraft.env.displayPhotoGui(stack.getTagCompound().getString(NBT_KEY), isNamed ? getNameImpl(stack) : null, !isNamed);
		}
		return stack;
	}

	@Override
	public PhotoStorage getPhotoStorage(final ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}
		
		return new AbstractPhotoStorage(true) {
			
			private final String photoId = PhotoManager.asString(stack.getTagCompound().getInteger(NBT_KEY));
			private final List<String> contents = ImmutableList.of(photoId); 
			
			@Override
			public int size() {
				return 1;
			}
			
			@Override
			public List<String> getPhotos() {
				return contents;
			}
			
			@Override
			public int capacity() {
				return 1;
			}
			
			@Override
			protected String getImpl(int index) {
				return photoId;
			}
			
			private int[] raw;
			
			@Override
			public int[] getRawPhotoIds() {
				return raw == null ? (raw = new int[] { PhotoManager.asInt(photoId) }) : raw;
			}
			
			// photos are immutable
			@Override
			protected void storeImpl(String photoId) { }
			
			@Override
			protected void removeImpl(int index) { }
			
			@Override
			protected void clearImpl() { }

		};
	}

	@Override
	public boolean isSealed(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack unseal(ItemStack sealed) {
		return sealed;
	}

	@Override
	public boolean canRewind(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack rewind(ItemStack stack) {
		return stack;
	}

	@Override
	public boolean canBeProcessed(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack process(ItemStack stack) {
		return stack;
	}

	@Override
	public boolean isScannable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isNamed(ItemStack stack) {
		return ItemStacks.getNbt(stack).hasKey(NBT_NAME_KEY);
	}

	@Override
	public String getName(ItemStack stack) {
		return isNamed(stack) ? getNameImpl(stack) : null;
	}

	private String getNameImpl(ItemStack stack) {
		return ItemStacks.getNbt(stack).getString(NBT_NAME_KEY);
	}

	@Override
	public void setName(ItemStack stack, String name) {
		if (!isNamed(stack)) {
			ItemStacks.getNbt(stack).setString(NBT_NAME_KEY, Strings.nullToEmpty(name));
		}
	}

}
