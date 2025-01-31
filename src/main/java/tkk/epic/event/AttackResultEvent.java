package tkk.epic.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class AttackResultEvent extends Event {
    public final LivingEntityPatch patch;
    public final AttackResult attackResult;
    public final EpicFightDamageSource damageSource;
    public final Entity target;
    public final InteractionHand hand;
    public AttackResultEvent(LivingEntityPatch entityPatch,AttackResult attackResult,EpicFightDamageSource damageSource, Entity target, InteractionHand hand){
        this.patch=entityPatch;
        this.attackResult=attackResult;
        this.damageSource=damageSource;
        this.target=target;
        this.hand=hand;
    }
}
