package mcjty.theoneprobe.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralCommandNode<CommandSourceStack> cmdTop = dispatcher.register(
                Commands.literal(TheOneProbe.MODID)
                        .then(CommandTopCfg.register(dispatcher))
                        .then(CommandTopNeed.register(dispatcher))
        );

        dispatcher.register(Commands.literal("top").redirect(cmdTop));
    }

}
