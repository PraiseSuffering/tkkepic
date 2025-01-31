package tkk.epic.capability.ai.module;

import net.minecraft.resources.ResourceLocation;
import tkk.epic.capability.ai.ITkkAiCapability;

public interface TkkAiModule {

    ResourceLocation getId();

    void runEvent(boolean moduleIsEnable, ITkkAiCapability entity, String event, Object[] args);

    int getExpect(boolean moduleIsEnable, ITkkAiCapability entity);

    void onEnable(ITkkAiCapability entity);

    void onDisable(ITkkAiCapability entity);


}
