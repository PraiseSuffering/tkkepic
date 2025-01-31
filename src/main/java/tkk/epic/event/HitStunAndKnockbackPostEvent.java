package tkk.epic.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class HitStunAndKnockbackPostEvent extends LivingEvent {
    public final Entity attackerEntity;
    public final EpicFightDamageSource epicFightDamageSource;
    public StunType stunType;
    public float stunTime;
    public float knockBackAmount;
    public HitStunAndKnockbackPostEvent(LivingEntity entity, Entity attackerEntity, EpicFightDamageSource epicFightDamageSource, StunType stunType, float stunTime,float knockBackAmount) {
        super(entity);
        this.attackerEntity=attackerEntity;
        this.epicFightDamageSource=epicFightDamageSource;
        this.stunType=stunType;
        this.stunTime=stunTime;
        this.knockBackAmount=knockBackAmount;
    }
}
