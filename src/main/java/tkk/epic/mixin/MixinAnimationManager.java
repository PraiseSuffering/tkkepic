package tkk.epic.mixin;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.capability.ai.module.TkkAiModuleManager;
import tkk.epic.skill.Skills;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;

import java.util.Map;

@Mixin(value = AnimationManager.class, remap = false)
public class MixinAnimationManager {

    @Inject(at = @At(value = "RETURN"), method = "Lyesman/epicfight/api/animation/AnimationManager;m_5944_(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/Map;", cancellable = false)
    private void Inject_m_5944_(ResourceManager resourceManager, ProfilerFiller profilerIn, CallbackInfoReturnable<Map<ResourceLocation, JsonElement>> cir) {
        Skills.reg();
        TkkAiModuleManager.reg();
    }
}
