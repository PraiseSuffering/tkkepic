package tkk.epic.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import javax.annotation.Nullable;


/**net.minecraftforge.event.entity.living.LivingAttackEvent*/
public class TryHurtEvent extends Event {
    public final LivingEntityPatch patch;
    public final DamageSource damageSource;
    public AttackResult attackResult;
    public float amount;
    public TryHurtEvent(LivingEntityPatch entityPatch, AttackResult attackResult, DamageSource damageSource,float amount){
        this.patch=entityPatch;
        this.attackResult=attackResult;
        this.damageSource=damageSource;
        this.amount=amount;
    }
    @Nullable
    public Entity getAttacker(){
        return damageSource.getEntity();
    }
    /**
     * @param type
     * 0 success
     * 1 blocked
     * 2 missed
     * */
    public void setAttackResult(int type,float amount){
        switch (type){
            case 0:
                attackResult=AttackResult.success(amount);
                return;
            case 1:
                attackResult=AttackResult.blocked(amount);
                return;
            case 2:
                attackResult=AttackResult.missed(amount);
                return;
        }
    }



}
