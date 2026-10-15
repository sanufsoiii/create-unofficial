package com.simibubi.create.content.logistics.box;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class PackageItem extends Item {
	public static final int SLOTS = 9;

	public PackageStyle style;

	public PackageItem(Properties properties) {
		this(properties, PackageStyles.STYLES.get(0));
	}

	public PackageItem(Properties properties, PackageStyle style) {
		super(properties);
		this.style = style;
		PackageStyles.ALL_BOXES.add(this);
		(style.rare() ? PackageStyles.RARE_BOXES : PackageStyles.STANDARD_BOXES).add(this);
	}

	@Override
	public String getDescriptionId() {
		return "item." + Create.ID + (style != null && style.rare() ? ".rare_package" : ".package");
	}

	public static boolean isPackage(ItemStack stack) {
		return stack.getItem() instanceof PackageItem || PackageItemHelper.isPackage(stack);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	public static ItemStack containing(List<ItemStack> stacks) {
		ItemStack box = PackageStyles.getRandomBox();
		box.set(AllDataComponents.PACKAGE_CONTENTS, ItemContainerContents.fromItems(stacks));
		return box;
	}

	public static void clearAddress(ItemStack box) {
		box.remove(AllDataComponents.PACKAGE_ADDRESS);
	}

	public static void addAddress(ItemStack box, String address) {
		box.set(AllDataComponents.PACKAGE_ADDRESS, address);
	}

	public static void setOrder(ItemStack box, int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex,
								boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
		PackageOrderData order = new PackageOrderData(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, orderContext);
		box.set(AllDataComponents.PACKAGE_ORDER_DATA, order);
	}

	public static int getOrderId(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			//noinspection DataFlowIssue
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).orderId();
		} else {
			return -1;
		}
	}

	public static boolean hasOrderData(ItemStack box) {
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA);
	}

	public static int getIndex(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			//noinspection DataFlowIssue
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).fragmentIndex();
		} else {
			return -1;
		}
	}

	public static boolean isFinal(ItemStack box) {
		//noinspection DataFlowIssue
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && box.get(AllDataComponents.PACKAGE_ORDER_DATA).isFinal();
	}

	public static int getLinkIndex(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			//noinspection DataFlowIssue
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).linkIndex();
		} else {
			return -1;
		}
	}

	public static boolean isFinalLink(ItemStack box) {
		//noinspection DataFlowIssue
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && box.get(AllDataComponents.PACKAGE_ORDER_DATA).isFinalLink();
	}

	@Nullable
	public static PackageOrderWithCrafts getOrderContext(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			PackageOrderData data = box.get(AllDataComponents.PACKAGE_ORDER_DATA);
			//noinspection DataFlowIssue
			return data.orderContext();
		} else if (box.has(AllDataComponents.PACKAGE_ORDER_CONTEXT)) {
			return box.get(AllDataComponents.PACKAGE_ORDER_CONTEXT);
		} else {
			return null;
		}
	}

	public static void addOrderContext(ItemStack box, PackageOrderWithCrafts orderContext) {
		box.set(AllDataComponents.PACKAGE_ORDER_CONTEXT, orderContext);
	}

	public static boolean matchAddress(ItemStack box, String address) {
		return PackageItemHelper.matchAddress(box, address);
	}

	public static boolean matchAddress(String boxAddress, String address) {
		return PackageItemHelper.matchAddress(boxAddress, address);
	}

	public static String getAddress(ItemStack box) {
		return PackageItemHelper.getAddress(box);
	}

	public static float getWidth(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi && pi.style != null)
			return pi.style.width() / 16f;
		return 1;
	}

	public static float getHeight(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi && pi.style != null)
			return pi.style.height() / 16f;
		return 1;
	}

	public static float getHookDistance(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi && pi.style != null)
			return pi.style.riggingOffset() / 16f;
		return 1;
	}
}
