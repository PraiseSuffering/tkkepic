package tkk.epic.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class HitStunAndKnockbackPreEvent extends LivingEvent {
    public final Entity attackerEntity;
    public final EpicFightDamageSource epicFightDamageSource;
    public StunType stunType;
    public float impact;

    public HitStunAndKnockbackPreEvent(LivingEntity entity,Entity attackerEntity,EpicFightDamageSource epicFightDamageSource,StunType stunType,float impact) {
        super(entity);
        this.attackerEntity=attackerEntity;
        this.epicFightDamageSource=epicFightDamageSource;
        this.stunType=stunType;
        this.impact=impact;
    }
}
