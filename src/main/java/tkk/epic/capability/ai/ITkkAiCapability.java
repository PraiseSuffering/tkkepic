package tkk.epic.capability.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tkk.epic.capability.ai.module.TkkAiModule;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public interface ITkkAiCapability {
    LivingEntity getEntity();

    CompoundTag getNbt();
    HashMap getMainTempData();
    HashMap getModuleTempData(ResourceLocation module);
    HashMap getTempData();

    void setModules(ResourceLocation[] modules);

    List<TkkAiModule> getModules();
    @Nullable TkkAiModule getNowModule();
    TkkAiModule getExpect(int expect);
    void updateExpect();

    void aiTick();
    void runEvent(String event, Object[] args);

    boolean isEnable();
    void setEnable(boolean enable);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);



}
