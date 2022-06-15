package mcjty.theoneprobe.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class Recipes extends FabricRecipeProvider {

    public Recipes(FabricDataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter) {

    }

}
