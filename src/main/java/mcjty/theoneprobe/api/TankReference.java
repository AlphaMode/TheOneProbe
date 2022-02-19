package mcjty.theoneprobe.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public final class TankReference {
    private final long capacity;
    private final long stored;
    private final List<StorageView<FluidVariant>> fluids;

    public TankReference(long capacity, long stored, List<StorageView<FluidVariant>> fluids) {
        this.capacity = capacity;
        this.stored = stored;
        this.fluids = fluids;
    }

    public TankReference(FriendlyByteBuf buffer) {
        capacity = buffer.readLong();
        stored = buffer.readLong();
        int size = buffer.readInt();
        fluids = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            FluidVariant fluidVariant = FluidVariant.fromPacket(buffer);
            long ammount = buffer.readLong();
            long capacity = buffer.readLong();
            fluids.add(i, new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return fluidVariant.isBlank();
                }

                @Override
                public FluidVariant getResource() {
                    return fluidVariant;
                }

                @Override
                public long getAmount() {
                    return ammount;
                }

                @Override
                public long getCapacity() {
                    return capacity;
                }
            });
        }
    }

    public long getCapacity() {
        return capacity;
    }

    public long getStored() {
        return stored;
    }

    public List<StorageView<FluidVariant>> getFluids() {
        return fluids;
    }

    /// Simple Self Simulated Tank or just a fluid display
    public static TankReference createSimple(long capacity, StorageView<FluidVariant> fluid) {
        return new TankReference(capacity, fluid.getAmount(), List.of(fluid));
    }

    /// Simple Tank like FluidTank
    public static TankReference createTank(SingleSlotStorage<FluidVariant> tank) {
        return new TankReference(tank.getCapacity(), tank.getAmount(), List.of(tank));
    }

    /// Any Fluid Handler, but Squashes all the fluids into 1 Progress Bar
    public static TankReference createHandler(Storage<FluidVariant> storage) {
        long capacity = 0;
        long stored = 0;
        List<StorageView<FluidVariant>> fluids = new ArrayList<>();
        try (Transaction t = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : storage.iterable(t)) {
                capacity += view.getCapacity();
                fluids.add(view);
                stored += view.getAmount();
            }
            t.abort();
        }
        return new TankReference(capacity, stored, fluids);
    }

    /// Any Fluid Handler but splits each internal Tank into its own Progress Bar
    public static TankReference[] createSplitHandler(Storage<FluidVariant> storage) {
        List<TankReference> references = new ArrayList<>();
        try(Transaction t = Transaction.openOuter()) {
            for(StorageView<FluidVariant> view : storage.iterable(t)) {
                references.add(new TankReference(view.getCapacity(), view.getAmount(), List.of(view)));
            }
        }
        return (TankReference[]) references.stream().toArray();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(capacity);
        buffer.writeLong(stored);
        buffer.writeInt(fluids.size());
		for (StorageView<FluidVariant> fluid : fluids) {
			fluid.getResource().toPacket(buffer);
            buffer.writeLong(fluid.getAmount());
            buffer.writeLong(fluid.getCapacity());
		}
    }
}
