package tkk.epic.capability.epicAdd.attackSpeed;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.units.qual.A;
import tkk.epic.network.SPEpicAddAttackSpeedUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class AttackSpeed {
    public final LivingEntity entity;
    public int updateTick=-999;
    public Float customAttackSpeed=null;
    public boolean attackSpeedModifiers=false;
    public Float preDelay_attackSpeedModifiers_set=null;
    public Float preDelay_attackSpeedModifiers_add=null;
    public Float preDelay_attackSpeedModifiers_scale=null;
    public Float contact_attackSpeedModifiers_set=null;
    public Float contact_attackSpeedModifiers_add=null;
    public Float contact_attackSpeedModifiers_scale=null;
    public Float recovery_attackSpeedModifiers_set=null;
    public Float recovery_attackSpeedModifiers_add=null;
    public Float recovery_attackSpeedModifiers_scale=null;
    public float getPreDelayAttackSpeed(float attackSpeed){
        return ((preDelay_attackSpeedModifiers_set==null)?attackSpeed:preDelay_attackSpeedModifiers_set)*((preDelay_attackSpeedModifiers_scale==null)?1.0f:preDelay_attackSpeedModifiers_scale)+((preDelay_attackSpeedModifiers_add==null)?0.0f:preDelay_attackSpeedModifiers_add);
    }
    public float getContactAttackSpeed(float attackSpeed){
        return ((contact_attackSpeedModifiers_set==null)?attackSpeed:contact_attackSpeedModifiers_set)*((contact_attackSpeedModifiers_scale==null)?1.0f:contact_attackSpeedModifiers_scale)+((contact_attackSpeedModifiers_add==null)?0.0f:contact_attackSpeedModifiers_add);
    }
    public float getRecoveryAttackSpeed(float attackSpeed){
        return ((recovery_attackSpeedModifiers_set==null)?attackSpeed:recovery_attackSpeedModifiers_set)*((recovery_attackSpeedModifiers_scale==null)?1.0f:recovery_attackSpeedModifiers_scale)+((recovery_attackSpeedModifiers_add==null)?0.0f:recovery_attackSpeedModifiers_add);
    }
    public AttackSpeed(LivingEntity entity){
        this.entity=entity;
    }

    public boolean isCustomAttackSpeed(){
        return customAttackSpeed!=null||attackSpeedModifiers;
    };

    public void setAttackSpeed(Float speed){
        this.customAttackSpeed=speed;
    };

    public float getAttackSpeed(InteractionHand hand){
        float attackSpeed=1.0F;
        EntityPatch patch=entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if (patch==null){return attackSpeed;}
        if(patch instanceof PlayerPatch){
            attackSpeed= ((PlayerPatch<?>) patch).getAttackSpeed(hand);
        }
        if(customAttackSpeed!=null){attackSpeed=customAttackSpeed;}

        switch (((LivingEntityPatch)patch).getEntityState().getLevel()){
            case 1:
                attackSpeed = this.getPreDelayAttackSpeed(attackSpeed);
                break;
            case 2:
                attackSpeed = this.getContactAttackSpeed(attackSpeed);
                break;
            case 3:
                attackSpeed = this.getRecoveryAttackSpeed(attackSpeed);
                break;
        }
        return attackSpeed;
    };

    public void setModifiersAttackSpeed(int level,Float set,Float add,Float scale){
        switch (level){
            case 1:
                preDelay_attackSpeedModifiers_set=set;
                preDelay_attackSpeedModifiers_add=add;
                preDelay_attackSpeedModifiers_scale=scale;
                break;
            case 2:
                contact_attackSpeedModifiers_set=set;
                contact_attackSpeedModifiers_add=add;
                contact_attackSpeedModifiers_scale=scale;
                break;
            case 3:
                recovery_attackSpeedModifiers_set=set;
                recovery_attackSpeedModifiers_add=add;
                recovery_attackSpeedModifiers_scale=scale;
                break;
        }
        attackSpeedModifiers= preDelay_attackSpeedModifiers_set != null || preDelay_attackSpeedModifiers_add != null || preDelay_attackSpeedModifiers_scale != null ||
                contact_attackSpeedModifiers_set != null || contact_attackSpeedModifiers_add != null || contact_attackSpeedModifiers_scale != null ||
                recovery_attackSpeedModifiers_set != null || recovery_attackSpeedModifiers_add != null || recovery_attackSpeedModifiers_scale != null;

    };

    public void clearModifiersAttackSpeed(){
        attackSpeedModifiers=false;
        preDelay_attackSpeedModifiers_set=null;
        preDelay_attackSpeedModifiers_add=null;
        preDelay_attackSpeedModifiers_scale=null;
        contact_attackSpeedModifiers_set=null;
        contact_attackSpeedModifiers_add=null;
        contact_attackSpeedModifiers_scale=null;
        recovery_attackSpeedModifiers_set=null;
        recovery_attackSpeedModifiers_add=null;
        recovery_attackSpeedModifiers_scale=null;
    }

    public void updateAttackSpeed(){
        updateTick=this.entity.tickCount;
        SPEpicAddAttackSpeedUpdata packet=new SPEpicAddAttackSpeedUpdata(this.entity,this);
        TkkEpicNetworkManager.sendToAllPlayerTrackingThisEntity(packet,this.entity);
        if(entity instanceof ServerPlayer){
            TkkEpicNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);
        }
    };
}
