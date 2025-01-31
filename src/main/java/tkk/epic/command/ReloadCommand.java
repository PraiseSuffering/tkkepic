package tkk.epic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.module.TkkAiModuleManager;
import tkk.epic.skill.Skills;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("reloadSkill").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
        builder.executes((commandSourceStack)->{
            TkkEpic.getInstance().broadcast("tkkepic reloadSkill");
            Skills.loadSkill();
            return 0;
        });
        dispatcher.register(Commands.literal("tkkepic").then(builder));

        builder = Commands.literal("reloadAiModule").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
        builder.executes((commandSourceStack)->{
            TkkEpic.getInstance().broadcast("tkkepic reloadAiModule");
            TkkAiModuleManager.loadModules();
            return 0;
        });
        dispatcher.register(Commands.literal("tkkepic").then(builder));
    }
}
