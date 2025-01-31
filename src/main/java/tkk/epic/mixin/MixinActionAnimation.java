package tkk.epic.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.animationCoord.CustomCoord;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = ActionAnimation.class, remap = true)
public abstract class MixinActionAnimation  extends MainFrameAnimation {
    public MixinActionAnimation(float convertTime, String path, Armature armature) {
        super(convertTime, path, armature);
    }


    @Inject(at = @At(value = "HEAD"), method = "Lyesman/epicfight/api/animation/types/ActionAnimation;begin(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;)V")
    public void inject_begin(LivingEntityPatch<?> entitypatch, CallbackInfo ci){
        IEpicAddCapability epicAddCap=entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAddCap!=null){
            epicAddCap.getCustomCoord().animationBegin();
        }

    }
    /**
     * @author
     */
    public void beginA(LivingEntityPatch<?> entitypatch) {
        super.begin(entitypatch);

        entitypatch.cancelAnyAction();

        if (entitypatch.shouldMoveOnCurrentSide((ActionAnimation) (Object)this)) {
            entitypatch.correctRotation();

            if (this.getProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT).orElse(false)) {
                entitypatch.getOriginal().setDeltaMovement(0.0D, entitypatch.getOriginal().getDeltaMovement().y, 0.0D);
                entitypatch.getOriginal().xxa = 0.0F;
                entitypatch.getOriginal().yya = 0.0F;
                entitypatch.getOriginal().zza = 0.0F;
            }

            MoveCoordFunctions.MoveCoordSetter moveCoordSetter = this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN).orElse(MoveCoordFunctions.RAW_COORD);
            IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if(cap!=null && cap.getCustomCoord().getTransformSheet()!=null){
                moveCoordSetter = CustomCoord.getTkkMoveCoordSetter(cap.getCustomCoord().moveType);
            }
            moveCoordSetter.set(this, entitypatch, entitypatch.getArmature().getActionAnimationCoord());
        }
    }
    /**
     * @author
     */
    @Overwrite
    protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        IEpicAddCapability cap = entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
        TimePairList coordUpdateTime = this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_UPDATE_TIME).orElse(null);
        boolean isCoordUpdateTime = coordUpdateTime == null || coordUpdateTime.isTimeInPairs(player.getElapsedTime());

        MoveCoordFunctions.MoveCoordSetter moveCoordsetter = isCoordUpdateTime ? this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK).orElse(null) : MoveCoordFunctions.RAW_COORD;
        if(cap.getCustomCoord().getTransformSheet()!=null){
            moveCoordsetter= CustomCoord.getTkkMoveCoordSetter(cap.getCustomCoord().moveType);
        }
        if (moveCoordsetter != null) {
            TransformSheet transformSheet = animation.isLinkAnimation() ? animation.getCoord() : entitypatch.getArmature().getActionAnimationCoord();
            moveCoordsetter.set(animation, entitypatch, transformSheet);
        }

        TransformSheet rootCoord;

        if (animation.isLinkAnimation()) {
            rootCoord = animation.getCoord();
        } else {
            rootCoord = entitypatch.getArmature().getActionAnimationCoord();

            if (rootCoord == null) {
                rootCoord = animation.getCoord();
            }
        }

        boolean hasNoGravity = entitypatch.getOriginal().isNoGravity();
        boolean moveVertical = this.getProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL).orElse(this.getProperty(AnimationProperty.ActionAnimationProperty.COORD).isPresent());
        MoveCoordFunctions.MoveCoordGetter moveGetter = isCoordUpdateTime ? this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_GET).orElse(MoveCoordFunctions.DIFF_FROM_PREV_COORD) : MoveCoordFunctions.DIFF_FROM_PREV_COORD;

        Vec3f move = moveGetter.get(animation, entitypatch, rootCoord);
        LivingEntity livingentity = entitypatch.getOriginal();
        Vec3 motion = livingentity.getDeltaMovement();

        this.getProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME).ifPresentOrElse((noGravityTime) -> {
            if (noGravityTime.isTimeInPairs(animation.isLinkAnimation() ? 0.0F : player.getElapsedTime())) {
                livingentity.setDeltaMovement(motion.x, 0.0D, motion.z);
            } else {
                move.y = 0.0F;
            }
        }, () -> {
            if (moveVertical && move.y > 0.0F && !hasNoGravity) {
                double gravity = livingentity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue();
                livingentity.setDeltaMovement(motion.x, motion.y <= 0.0F ? (motion.y + gravity) : motion.y, motion.z);
            }
        });

        Vec3 vec3 = move.toDoubleVector();
        if(!animation.isLinkAnimation() && cap.getCustomCoord().getTransformSheet()!=null && cap.getCustomCoord().canStopMove && entitypatch.shouldBlockMoving() ){
            vec3 = vec3.scale(0.0D);
        }
        return vec3;
    }


}
