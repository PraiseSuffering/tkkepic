package tkk.epic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StateSpectrum;
import yesman.epicfight.api.utils.datastruct.TypeFlexibleHashMap;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = ServerAnimator.class, remap = true)
public interface MixinServerAnimator {
    @Accessor("nextPlaying")
    DynamicAnimation getNextPlaying();

}
