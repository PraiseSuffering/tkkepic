package tkk.epic.capability.tkk;

import net.minecraft.nbt.CompoundTag;
import tkk.epic.skill.SkillContainer;

public interface ITkkCapability{
    //id 0-SKILL_SIZE
    SkillContainer getSkillContainer(int id);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);



}
