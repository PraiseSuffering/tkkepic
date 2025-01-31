package tkk.epic.gameasset.aiModules;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.ITkkAiCapability;
import tkk.epic.capability.ai.TkkAiHelper;
import tkk.epic.capability.ai.TkkAiTickEventGoal;
import tkk.epic.capability.ai.module.TkkAiModule;
import tkk.epic.capability.ai.module.TkkAiModuleEventName;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Set;

public class MoveToTargetModule implements TkkAiModule {
    public static final ResourceLocation ID=new ResourceLocation(TkkEpic.MODID,"move_to_target");
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void runEvent(boolean moduleIsEnable, ITkkAiCapability entity, String event, Object[] args) {
        if(moduleIsEnable && event.equals(TkkAiModuleEventName.AI_TICK)){
            if(!(entity.getEntity() instanceof Mob)){
                return;
            }
            Entity target= ((Mob) entity.getEntity()).getTarget();
            if (target==null){return;}
            LivingEntityPatch patch= (LivingEntityPatch) entity.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
            if (patch.getEntityState().movementLocked()){return;}
            TkkAiHelper.moveTo(((Mob) entity.getEntity()),1.0,target );
        }
    }

    @Override
    public int getExpect(boolean moduleIsEnable, ITkkAiCapability entity) {
        if(!(entity.getEntity() instanceof Mob)){
            return 0;
        }
        if((((Mob) entity.getEntity()).getTarget())==null){return 0;}
        LivingEntityPatch patch= (LivingEntityPatch) entity.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if (patch.getEntityState().movementLocked()){return 0;}
        return 1;
    }

    @Override
    public void onEnable(ITkkAiCapability entity) {

    }

    @Override
    public void onDisable(ITkkAiCapability entity) {

    }
}
