package tkk.epic.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.TkkEpic;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.TryHurtEvent;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@Mixin(value = LivingEntityPatch.class, remap = true)
public class MixinLivingEntityPatch {
    @Inject(method = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;attack(Lyesman/epicfight/world/damagesource/EpicFightDamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lyesman/epicfight/api/utils/AttackResult;", at = @At("RETURN"))
    private void inject_attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand, CallbackInfoReturnable<AttackResult> cir) {
        AttackResultEvent event=new AttackResultEvent((LivingEntityPatch) (Object)this,cir.getReturnValue(),damageSource,target,hand);
        MinecraftForge.EVENT_BUS.post(event);
    }
    @Inject(method = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;tryHurt(Lnet/minecraft/world/damagesource/DamageSource;F)Lyesman/epicfight/api/utils/AttackResult;",at = @At("RETURN"),cancellable = true)
    private void inject_tryHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<AttackResult> cir){
        TryHurtEvent event=new TryHurtEvent((LivingEntityPatch) (Object)this,cir.getReturnValue(),damageSource,amount);
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.attackResult);
    }
}
