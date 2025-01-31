package tkk.epic.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.event.AnimationEndEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.Optional;

@Mixin(value = AttackAnimation.class, remap = true)
public abstract class MixinAttackAnimation extends ActionAnimation {

    public MixinAttackAnimation(float convertTime, String path, Armature armature) {
        super(convertTime, path, armature);
    }

    @Shadow abstract AttackAnimation.Phase getPhaseByTime(float elapsedTime);

    @Inject(method = "Lyesman/epicfight/api/animation/types/AttackAnimation;getPlaySpeed(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/DynamicAnimation;)F",at = @At("HEAD"),cancellable = true)
    public void inject_getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation, CallbackInfoReturnable<Float> cir) {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (cap==null){return;}
        if(!cap.isCustomAttackSpeed()){return;}

        AttackAnimation.Phase phase = this.getPhaseByTime(entitypatch.getAnimator().getPlayerFor(this).getElapsedTime());
        float speedFactor = this.getProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR).orElse(1.0F);
        Optional<Float> property = this.getProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED);
        float correctedSpeed = property.map((value) -> cap.getAttackSpeed(phase.hand) / value).orElse(this.getTotalTime() * cap.getAttackSpeed(phase.hand));
        correctedSpeed = Math.round(correctedSpeed * 1000.0F) / 1000.0F;
        cir.setReturnValue(1.0F + (correctedSpeed - 1.0F) * speedFactor);
    }
    @Inject(method = "getEpicFightDamageSource(Lnet/minecraft/world/damagesource/DamageSource;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/entity/Entity;Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;)Lyesman/epicfight/world/damagesource/EpicFightDamageSource;", at = @At("RETURN"), cancellable = true)
    private void inject_(DamageSource originalSource, LivingEntityPatch<?> entitypatch, Entity target, AttackAnimation.Phase phase,CallbackInfoReturnable<EpicFightDamageSource> cir) {
        //cir.setReturnValue(cir.getReturnValue() * 3);
        //cir.getReturnValue().setDamageModifier(new ValueModifier(10,1,Float.NaN));
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (cap==null){return;}
        if(!cap.getCDS().enable){return;}
        cir.setReturnValue(cap.getCDS().getDamageSource(cir.getReturnValue()));
    }
}
