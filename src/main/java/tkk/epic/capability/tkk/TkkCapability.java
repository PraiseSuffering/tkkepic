package tkk.epic.capability.tkk;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import tkk.epic.skill.SkillContainer;

import static tkk.epic.gui.hud.hotbar.HotBar.SKILL_SIZE;

public class TkkCapability implements ITkkCapability{

    private SkillContainer[] containers;

    public TkkCapability(ServerPlayer player){
        containers=new SkillContainer[SKILL_SIZE];
        for(int i=0;i<SKILL_SIZE;i++){
            containers[i]=new SkillContainer(player,i);
        }
    }


    @Override
    public SkillContainer getSkillContainer(int id) {
        return containers[id];
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        for(int i=0;i<SKILL_SIZE;i++){
            compound.put(i+"",containers[i].serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for(int i=0;i<SKILL_SIZE;i++){
            containers[i].deserializeNBT(nbt.getCompound(i+""));
        }
    }
}
