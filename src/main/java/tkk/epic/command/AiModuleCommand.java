package tkk.epic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.ITkkAiCapability;
import tkk.epic.capability.ai.TkkAiCapabilityProvider;
import tkk.epic.capability.ai.module.TkkAiModuleManager;
import tkk.epic.skill.Skills;

import java.util.Collection;

public class AiModuleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("clear").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
        builder.then(Commands.argument("targets", EntityArgument.entities()).executes((p_137810_) -> {
            return clearAiModule(EntityArgument.getEntities(p_137810_, "targets"));
        }));
        dispatcher.register(Commands.literal("tkkaimodule").then(builder));
        builder = Commands.literal("set").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
        builder.then(Commands.argument("moodules", StringArgumentType.string()).
                then(Commands.argument("targets", EntityArgument.entities()).
                        executes((p_137810_) -> {
            return setAiModule(p_137810_.getSource(),EntityArgument.getEntities(p_137810_, "targets"),StringArgumentType.getString(p_137810_, "moodules"));
        })));
        dispatcher.register(Commands.literal("tkkaimodule").then(builder));
        builder = Commands.literal("list").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
        builder.executes((p_137810_) -> {
            ResourceLocation[] list=getAiModuleList();
            StringBuffer stringBuffer=new StringBuffer();
            for (ResourceLocation rl:list){
                stringBuffer.append("\n"+rl.toString());
            }

            p_137810_.getSource().sendSystemMessage(Component.translatable("commands.tkkaimodule.list", stringBuffer.toString()));
            return 0;
        });
        dispatcher.register(Commands.literal("tkkaimodule").then(builder));

    }

    public static int clearAiModule(Collection<? extends Entity> entities){
        ITkkAiCapability cap;
        int i=0;
        for(Entity entity : entities) {
            cap=entity.getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
            if (cap==null){continue;}
            cap.setModules(new ResourceLocation[0]);
            cap.setEnable(false);
            i++;
        }
        return i;
    }
    public static int setAiModule(CommandSourceStack commandSourceStack,Collection<? extends Entity> entities,String modules){
        String[] moduleString=modules.split(",");
        ResourceLocation[] module=new ResourceLocation[moduleString.length];
        for (int i=0;i<moduleString.length;i++){
            module[i]=new ResourceLocation(moduleString[i]);
            if (TkkAiModuleManager.getModule(module[i])==null){
                commandSourceStack.sendFailure(Component.translatable("commands.tkkaimodule.set.notfind", moduleString[i]));
                return 0;
            }
        }
        ITkkAiCapability cap;
        int i=0;
        for(Entity entity : entities) {
            cap=entity.getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
            if (cap==null){continue;}
            cap.setModules(module);
            cap.setEnable(true);
            i++;
        }
        return i;
    }
    public static ResourceLocation[] getAiModuleList(){
        return TkkAiModuleManager.MODULES.keySet().toArray(new ResourceLocation[0]);
    }
}
