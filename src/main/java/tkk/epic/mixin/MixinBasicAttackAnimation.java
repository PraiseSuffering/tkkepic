package tkk.epic.mixin;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = BasicAttackAnimation.class,remap = true)
public class MixinBasicAttackAnimation  extends AttackAnimation {
    public MixinBasicAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    /**
     * @author
     */
    @Overwrite
    protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation dynamicAnimation) {
        Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        boolean shouldBlockMoving=entitypatch.shouldBlockMoving();
        if(cap!=null && cap.getShouldBlockMoving().shouldBlockMoving!=null){
            shouldBlockMoving=cap.getShouldBlockMoving().shouldBlockMoving;
        }
        if (shouldBlockMoving && (Boolean)this.getProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
            if(cap!=null && cap.getCustomCoord().canStopMove){
                vec3 = vec3.scale(0.0D);
            }
        }

        return vec3;
    }
}
