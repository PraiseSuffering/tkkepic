package tkk.epic.mixin;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.events.EntityEvents;


@Mixin(value = EntityEvents.class, remap = true)
public class MixinEntityEvents {

    @Inject(at = @At(value = "HEAD"), method = "hurtEvent(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", cancellable = true)
    private static void Inject_hurtEvent(LivingHurtEvent event,CallbackInfo info) {
        HandlerEntityEvents.hurtEvent(event);
        info.cancel();
    }
}
