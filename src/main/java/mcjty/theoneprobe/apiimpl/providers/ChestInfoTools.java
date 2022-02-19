package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.lib.TransferHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ChestInfoTools {

    static void showChestInfo(ProbeMode mode, IProbeInfo probeInfo, Level world, BlockPos pos, IProbeConfig config) {
        List<ItemStack> stacks = null;
        IProbeConfig.ConfigMode chestMode = config.getShowChestContents();
        if (chestMode == IProbeConfig.ConfigMode.EXTENDED && (Config.showSmallChestContentsWithoutSneaking.get() > 0 || !Config.getInventoriesToShow().isEmpty())) {
            if (Config.getInventoriesToShow().contains(Registry.BLOCK.getKey(world.getBlockState(pos).getBlock()))) {
                chestMode = IProbeConfig.ConfigMode.NORMAL;
            } else if (Config.showSmallChestContentsWithoutSneaking.get() > 0) {
                stacks = new ArrayList<>();
                int slots = getChestContents(world, pos, stacks);
                if (slots <= Config.showSmallChestContentsWithoutSneaking.get()) {
                    chestMode = IProbeConfig.ConfigMode.NORMAL;
                }
            }
        } else if (chestMode == IProbeConfig.ConfigMode.NORMAL && !Config.getInventoriesToNotShow().isEmpty()) {
            if (Config.getInventoriesToNotShow().contains(Registry.BLOCK.getKey(world.getBlockState(pos).getBlock()))) {
                chestMode = IProbeConfig.ConfigMode.EXTENDED;
            }
        }

        if (Tools.show(mode, chestMode)) {
            if (stacks == null) {
                stacks = new ArrayList<>();
                getChestContents(world, pos, stacks);
            }

            if (!stacks.isEmpty()) {
                boolean showDetailed = Tools.show(mode, config.getShowChestContentsDetailed()) && stacks.size() <= Config.showItemDetailThresshold.get();
                showChestContents(probeInfo, world, pos, stacks, showDetailed);
            }
        }
    }

    public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
        if (first.isEmpty() || !first.sameItem(second) || first.hasTag() != second.hasTag()) return false;

        return !first.hasTag() || first.getTag().equals(second.getTag());
    }

    private static void addItemStack(List<ItemStack> stacks, Set<Item> foundItems, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (foundItems != null && foundItems.contains(stack.getItem())) {
            for (ItemStack s : stacks) {
                if (canItemStacksStack(s, stack)) {
                    s.grow(stack.getCount());
                    return;
                }
            }
        }
        // If we come here we need to append a new stack
        stacks.add(stack.copy());
        if (foundItems != null) {
            foundItems.add(stack.getItem());
        }
    }

    private static void showChestContents(IProbeInfo probeInfo, Level world, BlockPos pos, List<ItemStack> stacks, boolean detailed) {
        int rows = 0;
        int idx = 0;

        IProbeInfo horizontal = null;
        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));

        if (detailed) {
            horizontal.text("These results are not accurate!");
            horizontal.text("Due to limitations with the transfer api");
            for (ItemStack stackInSlot : stacks) {
                horizontal = vertical.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                horizontal.item(stackInSlot, new ItemStyle().width(16).height(16))
                        .text(CompoundText.create().info(stackInSlot.getDescriptionId()));
            }
        } else {
            for (ItemStack stackInSlot : stacks) {
                if (idx % 10 == 0) {
                    horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) {
                        break;
                    }
                }
                horizontal.item(stackInSlot);
                idx++;
            }
        }
    }

    private static int getChestContents(Level world, BlockPos pos, List<ItemStack> stacks) {
        BlockEntity te = world.getBlockEntity(pos);

        Set<Item> foundItems = Config.compactEqualStacks.get() ? new HashSet<>() : null;
        AtomicInteger maxSlots = new AtomicInteger();
        try {
            if (te != null && TransferHelper.getItemStorage(te) != null) {
               try(Transaction t = Transaction.openOuter()) {
                   int max = 0;
                   for (StorageView<ItemVariant> view : TransferHelper.getItemStorage(te).iterable(t)) {
                       addItemStack(stacks, foundItems, view.getResource().toStack());
                        max++;
                   }
                   maxSlots.set(max);
               }
            } else if (te instanceof Container inventory) {
                maxSlots.set(inventory.getContainerSize());
                for (int i = 0; i < maxSlots.get(); i++) {
                    addItemStack(stacks, foundItems, inventory.getItem(i));
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Getting the contents of a " + Registry.BLOCK.getKey(world.getBlockState(pos).getBlock()) + " (" + te.getClass().getName() + ")", e);
        }
        return maxSlots.get();
    }
}
