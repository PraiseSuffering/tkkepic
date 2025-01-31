package tkk.epic.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.module.TkkAiModule;

import java.util.Map;

public class RegisterAiModuleEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, TkkAiModule> modules;
    public RegisterAiModuleEvent(Map<ResourceLocation, TkkAiModule> map){
        modules=map;
    }

    public void regModule(ResourceLocation id,TkkAiModule module){
        if (modules.containsKey(id)){
            TkkEpic.LOGGER.warn("[TkkAI] Duplicate id:"+id);
        }
        modules.put(id,module);
    }

}
