package mcjty.theoneprobe.lib;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class FluidStack {
    private final FluidVariant resource;
    private long amount;
    public FluidStack(FluidVariant resource, long amount) {
        this.resource = resource;
        this.amount = amount;
    }

    public FluidVariant getResource() {
        return resource;
    }

    public long getAmount() {
        return amount;
    }
}
