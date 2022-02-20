package mcjty.theoneprobe.lib;

import mcjty.theoneprobe.config.Config;

public enum FluidUnit {
    DROPLETS(1),
    MILIBUCKETS(81);

    private final int oneBucket;

    FluidUnit(int bucketAmount) {
        this.oneBucket = bucketAmount;
    }

    public int getOneBucketAmount() {
        return oneBucket;
    }

    /**
     * Convert a non negative fluid amount in droplets to a unicode string
     * representing the amount in millibuckets. For example, passing 163 will result
     * in
     *
     * <pre>
     * 2 ¹⁄₈₁
     * </pre>
     *
     * .
     */
    public static long getAmountFromDroplets(long droplets) {
        FluidUnit fluidUnit = Config.cfgFluidUnit.get();
        if(fluidUnit == FluidUnit.DROPLETS)
            return droplets;

        return droplets / fluidUnit.getOneBucketAmount();
    }
}
