package mcjty.theoneprobe;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.ProbeMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.EXTENDED;
import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.NORMAL;

public class Tools {


    public static String getModName(EntityType<?> entry) {
        ResourceLocation registryName = Registry.ENTITY_TYPE.getKey(entry);
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return FabricLoader.getInstance().getModContainer(modId)
                .map(mod -> mod.getMetadata().getName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static String getModName(Block entry) {
        ResourceLocation registryName = Registry.BLOCK.getKey(entry);
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return FabricLoader.getInstance().getModContainer(modId)
                .map(mod -> mod.getMetadata().getName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static boolean show(ProbeMode mode, IProbeConfig.ConfigMode cfg) {
        return cfg == NORMAL || (cfg == EXTENDED && mode == ProbeMode.EXTENDED);
    }
}
