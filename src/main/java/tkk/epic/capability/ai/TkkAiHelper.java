package tkk.epic.capability.ai;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import tkk.epic.TkkEpic;
import tkk.epic.utils.MathHelper;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.Objects;

public class TkkAiHelper {
    public static void moveTo(Mob entity, double speedModifier, Entity target){
        entity.getNavigation().moveTo(target,speedModifier);
    }
    public static void moveTo(Mob entity, double speedModifier, BlockPos target){
        entity.getNavigation().moveTo(target.getX(),target.getY(),target.getZ(),speedModifier);
    }
    public static void moveTo(Mob entity, double speedModifier, Vec3 target){
        entity.getNavigation().moveTo(target.x(),target.y(),target.z(),speedModifier);
    }
    public static void moveTo(Mob entity, double speedModifier,double x,double y,double z){
        entity.getNavigation().moveTo(x,y,z,speedModifier);
    }
    public static void stopMove(Mob entity){
        entity.getNavigation().stop();
    }

    public static void strafe(Mob entity,float strafeForwards, float strafeRight){
        entity.getMoveControl().strafe(strafeForwards,strafeRight);
    }

    public static void lookAt(Entity entity, Vec3 target){
        entity.lookAt(EntityAnchorArgument.Anchor.FEET,target);
        double[] yp= MathHelper.inverseDotYP(target.x()-entity.getX(), target.y()-entity.getY(),target.z()-entity.getZ());
        entity.setYRot((float) (yp[0]));
        entity.setXRot((float) (yp[1]));
        entity.yRotO= (float) yp[0];
        entity.setYBodyRot((float) yp[0]);
        entity.setYHeadRot((float) yp[0]);
        entity.xRotO= (float) yp[1];
    }
    public static void lookAt(Entity entity, Entity target){
        lookAt(entity,target.position());
    }
    public static void lookAt(Entity entity, BlockPos target){
        lookAt(entity,target.getCenter());
    }
    public static void lookAt(Entity entity, double x,double y,double z){
        lookAt(entity,new Vec3(x,y,z));
    }


    public static boolean isGuard(Entity entity){
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(patch==null || !(patch instanceof PlayerPatch)){return false;}
        SkillContainer guardSkill=((PlayerPatch)patch).getSkill(SkillSlots.GUARD);
        CapabilityItem itemCapability = ((PlayerPatch)patch).getHoldingItemCapability(((Player) entity).getUsedItemHand());
        if(itemCapability.getUseAnimation((LivingEntityPatch<?>) patch) == UseAnim.BLOCK && ((Player)entity).isUsingItem() && guardSkill.getSkill().isExecutableState((PlayerPatch<?>) patch)){
            return true;
        }
        return false;
    }

    public static boolean isDodge(Entity entity){
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(patch==null || !(patch instanceof LivingEntityPatch)){return false;}
        if(((LivingEntityPatch) patch).getAnimator().getPlayerFor(null).getAnimation() instanceof DodgeAnimation){return true;}
        if(((LivingEntityPatch) patch).getAnimator().getPlayerFor(null).getAnimation() instanceof LinkAnimation){
            if(((LinkAnimation)(((LivingEntityPatch) patch).getAnimator().getPlayerFor(null).getAnimation())).getNextAnimation() instanceof DodgeAnimation){
                return true;
            }
        }
        return false;
    }
    public static int getResidueAnticipationTime(Entity entity,int checkTick){
        int temp=getResidueTime(entity, EntityState.PHASE_LEVEL,2,checkTick);
        if (temp!=-1){return temp;}
        temp=getResidueTime(entity, EntityState.PHASE_LEVEL,3,checkTick);
        return temp;
    }
    public static int getResidueTime(Entity entity, EntityState.StateFactor targetKey,Object targetValue,int checkTick){
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(patch==null || !(patch instanceof LivingEntityPatch)){return -1;}
        var animationPlayer=((LivingEntityPatch<?>) patch).getServerAnimator().animationPlayer;
        if(animationPlayer.getAnimation()==null){return -1;}
        boolean reversed=animationPlayer.isReversed() && animationPlayer.getAnimation().canBePlayedReverse();
        float step=animationPlayer.getAnimation().getPlaySpeed((LivingEntityPatch<?>) patch,animationPlayer.getAnimation()) * EpicFightOptions.A_TICK;
        float length=animationPlayer.getElapsedTime();
        if(reversed){
            length=animationPlayer.getAnimation().getTotalTime()-animationPlayer.getElapsedTime();
        }
        DynamicAnimation animation=animationPlayer.getAnimation();
        int add=0;
        if(animation.isLinkAnimation()){
            add=getToEndTimeSkipLinkAnimationCalculate(entity);
            length=((LinkAnimation)animation).getNextStartTime()+step;
            animation=((LinkAnimation)animation).getNextAnimation();
        }
        for (int i=0;i<=checkTick;i++){
            Object temp=animation.getState(targetKey, (LivingEntityPatch<?>) patch,length+i*step);
            if (Objects.equals(targetValue,temp)){
                return i+add;
            }
        }
        return -1;

    }
    public static int getToEndTime(Entity entity){
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(patch==null || !(patch instanceof LivingEntityPatch)){return -1;}
        var animationPlayer=((LivingEntityPatch<?>) patch).getServerAnimator().animationPlayer;
        if(animationPlayer.getAnimation()==null){return -1;}
        boolean reversed=animationPlayer.isReversed() && animationPlayer.getAnimation().canBePlayedReverse();
        float step=animationPlayer.getAnimation().getPlaySpeed((LivingEntityPatch<?>) patch,animationPlayer.getAnimation()) * EpicFightOptions.A_TICK;
        float length=animationPlayer.getElapsedTime();
        if(reversed){
            length=animationPlayer.getAnimation().getTotalTime()-animationPlayer.getElapsedTime();
        }
        float time=animationPlayer.getAnimation().getTotalTime()-length;
        if(animationPlayer.getAnimation().isLinkAnimation()){
            time+=((LinkAnimation)animationPlayer.getAnimation()).getNextAnimation().getTotalTime()-((LinkAnimation)animationPlayer.getAnimation()).getNextStartTime();
        }
        return (int) Math.ceil(time/step);
    }
    public static int getToEndTimeSkipLinkAnimationCalculate(Entity entity){
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(patch==null || !(patch instanceof LivingEntityPatch)){return -1;}
        var animationPlayer=((LivingEntityPatch<?>) patch).getServerAnimator().animationPlayer;
        if(animationPlayer.getAnimation()==null){return -1;}
        boolean reversed=animationPlayer.isReversed() && animationPlayer.getAnimation().canBePlayedReverse();
        float step=animationPlayer.getAnimation().getPlaySpeed((LivingEntityPatch<?>) patch,animationPlayer.getAnimation()) * EpicFightOptions.A_TICK;
        float length=animationPlayer.getElapsedTime();
        if(reversed){
            length=animationPlayer.getAnimation().getTotalTime()-animationPlayer.getElapsedTime();
        }
        float time=animationPlayer.getAnimation().getTotalTime()-length;
        return (int) Math.ceil(time/step);
    }

}
