package mcjty.theoneprobe.lib;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class TransferHelper {
    public static Storage<FluidVariant> getFluidStorage(BlockEntity be) {
        Level l = be.getLevel();
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();

        for (Direction direction : Direction.values()) {
            Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(l, pos, state, be, direction);

            if (fluidStorage != null) {
                return fluidStorage;
            }
        }
        return null;
    }

    public static Storage<ItemVariant> getItemStorage(BlockEntity be) {
        List<Storage<ItemVariant>> itemStorages = new ArrayList<>();
        Level l = be.getLevel();
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();
        if(be instanceof InventoryStorage)

        for (Direction direction : Direction.values()) {
            Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(l, pos, state, be, direction);

            if (itemStorage != null) {
                if (itemStorages.size() == 0) {
                    itemStorages.add(itemStorage);
                    continue;
                }

                for (Storage<ItemVariant> storage : itemStorages) {
                    if (!storage.equals(itemStorage)) {
                        itemStorages.add(itemStorage);
                        break;
                    }
                }
            }
        }

        if (itemStorages.isEmpty()) return null;
        if (itemStorages.size() == 1) return itemStorages.get(0);
        return new CombinedStorage<>(itemStorages);
    }

}
