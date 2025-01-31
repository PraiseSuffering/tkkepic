package tkk.epic.gameasset.aiModules;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.ITkkAiCapability;
import tkk.epic.capability.ai.TkkAiTickEventGoal;
import tkk.epic.capability.ai.module.TkkAiModule;
import tkk.epic.capability.ai.module.TkkAiModuleEventName;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

import java.util.Set;

public class ClearGoalSelectorModule implements TkkAiModule {
    public static final ResourceLocation ID=new ResourceLocation(TkkEpic.MODID,"clear_goal_selector");
    public static void clearGoal(Entity entity){
        if(!(entity instanceof Mob)){
            return;
        }
        GoalSelector goalSelector=((Mob) entity).goalSelector;

        Set<Goal> toRemove = Sets.newHashSet();
        for (WrappedGoal wrappedGoal : goalSelector.getAvailableGoals()) {
            Goal goal = wrappedGoal.getGoal();

            if (!(goal instanceof TkkAiTickEventGoal)) {
                toRemove.add(goal);
            }
        }
        toRemove.forEach(goalSelector::removeGoal);
        //TkkEpic.LOGGER.error("clear goal:"+toRemove);
    }
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void runEvent(boolean moduleIsEnable, ITkkAiCapability entity, String event, Object[] args) {
        if(event.equals(TkkAiModuleEventName.ENTITY_JOIN_LEVEL_EVENT) || event.equals(TkkAiModuleEventName.AI_ENABLE) || event.equals(TkkAiModuleEventName.LIVING_EQUIPMENT_CHANGE_EVENT) || event.equals(TkkAiModuleEventName.ENTITY_MOUNT_EVENT)){
            clearGoal(entity.getEntity());
        }
    }

    @Override
    public int getExpect(boolean moduleIsEnable, ITkkAiCapability entity) {
        return 0;
    }

    @Override
    public void onEnable(ITkkAiCapability entity) {

    }

    @Override
    public void onDisable(ITkkAiCapability entity) {

    }
}
