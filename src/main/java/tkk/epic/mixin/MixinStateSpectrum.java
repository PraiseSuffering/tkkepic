package tkk.epic.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.datastruct.TypeFlexibleHashMap;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@Mixin(value = StateSpectrum.class, remap = true)
public abstract class MixinStateSpectrum {


    @Inject(method = "Lyesman/epicfight/api/animation/types/StateSpectrum;getSingleState(Lyesman/epicfight/api/animation/types/EntityState$StateFactor;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;F)Ljava/lang/Object;", at = @At("RETURN"), cancellable = true)
    private <T> void inject_getSingleState(EntityState.StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time, CallbackInfoReturnable<T> cir) {
        //cir.setReturnValue(cir.getReturnValue() * 3);
        //cir.getReturnValue().setDamageModifier(new ValueModifier(10,1,Float.NaN));
        if(entitypatch.getAnimator().getPlayerFor(null).getAnimation().isRepeat()){return;}//idle类不修改
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (cap==null){return;}
        cir.setReturnValue(cap.getCustomStateSpectrum().getSingleState(cir.getReturnValue(),stateFactor,entitypatch,time));
    }
    @Inject(method = "Lyesman/epicfight/api/animation/types/StateSpectrum;getStateMap(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;F)Lyesman/epicfight/api/utils/datastruct/TypeFlexibleHashMap;", at = @At("RETURN"), cancellable = true)
    private void inject_getStateMap(LivingEntityPatch<?> entitypatch, float time, CallbackInfoReturnable<TypeFlexibleHashMap<EntityState.StateFactor<?>>> cir) {
        //cir.setReturnValue(cir.getReturnValue() * 3);
        //cir.getReturnValue().setDamageModifier(new ValueModifier(10,1,Float.NaN));
        if(entitypatch.getAnimator().getPlayerFor(null).getAnimation().isRepeat()){return;}//idle类不修改
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (cap==null){return;}
        cir.setReturnValue(cap.getCustomStateSpectrum().getStateMap(cir.getReturnValue(),entitypatch,time));
    }

}
