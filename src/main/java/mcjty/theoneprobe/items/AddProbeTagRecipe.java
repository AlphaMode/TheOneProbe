package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ObjectHolder;

public class AddProbeTagRecipe extends AbstractRecipeAdaptor {

    @ObjectHolder(TheOneProbe.MODID + ":probe_helmet")
    public static AddProbeTagRecipeSerializer HELMET_SERIALIZER;

    public AddProbeTagRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack result = recipe.assemble(inv);
        CompoundTag tc = new CompoundTag();
        tc.putInt(ModItems.PROBETAG, 1);
        result.setTag(tc);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HELMET_SERIALIZER;
    }
}
