package tkk.epic.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.epicAdd.trail.ParticleTrail;
import tkk.epic.capability.epicAdd.trail.TkkTrail;
import tkk.epic.event.AnimationEndEvent;
import tkk.epic.particle.TkkParticles;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.animation.types.StateSpectrum;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.AnimationBeginEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

@Mixin(value = StaticAnimation.class, remap = true)
public abstract class MixinStaticAnimation  extends DynamicAnimation implements AnimationProvider<StaticAnimation> {


    @Shadow private int animationId;
    @Shadow private Armature armature;

    @Shadow public abstract boolean equals(Object obj);

    @Shadow public abstract ResourceLocation getRegistryName();

    /**
     * @author
     */
    //@Overwrite
    public void begin(LivingEntityPatch<?> entitypatch) {
        this.getAnimationClip();
        this.getProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS).ifPresent((events) -> {
            AnimationEvent[] var3 = events;
            int var4 = events.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                AnimationEvent event = var3[var5];
                event.executeIfRightSide(entitypatch, (StaticAnimation) ((Object)this));
            }

        });
        if ( entitypatch.isLogicalClient()) {
            Consumer consumer=(a)->{doTrail(entitypatch);};
            consumer.accept(entitypatch);
        }

        if (entitypatch instanceof PlayerPatch) {
            PlayerPatch<?> playerpatch = (PlayerPatch)entitypatch;
            playerpatch.getEventListener().triggerEvents(PlayerEventListener.EventType.ANIMATION_BEGIN_EVENT, new AnimationBeginEvent(playerpatch, (StaticAnimation) ((Object)this)));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void doTrail(LivingEntityPatch<?> entitypatch){
        IEpicAddCapability epicAddCap=entitypatch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        boolean doVanilla=true;
        if(epicAddCap!=null && (((Object)this) instanceof MainFrameAnimation)){
            epicAddCap.getTkkCustomTrail().readyAnimation();
            doVanilla=epicAddCap.getTkkCustomTrail().readyDoVanillaTrail;
            //TkkEpic.getInstance().broadcast("this.getProperty(ClientAnimationProperties.TRAIL_EFFECT) "+(this.getProperty(ClientAnimationProperties.TRAIL_EFFECT).orElse(null) != null));
            if(this.armature!=null && this.animationId!=-1) {
                //if(entitypatch instanceof PlayerPatch){TkkEpic.getInstance().broadcast("playTrail");}
                int idx = 0;
                for (TkkTrail tkkTrail : epicAddCap.getTkkCustomTrail().readyTrails) {
                    double eid = Double.longBitsToDouble((long) ((LivingEntity) entitypatch.getOriginal()).getId());
                    double animid = Double.longBitsToDouble((long) this.animationId);
                    double jointId = Double.longBitsToDouble((long) this.armature.searchJointByName(tkkTrail.joint).getId());
                    double index = Double.longBitsToDouble((long) (idx++));
                    InteractionHand hand=tkkTrail.hand? InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
                    ItemSkin itemSkin=null;
                    if (hand != null) {
                        ItemStack stack = ((LivingEntity) entitypatch.getOriginal()).getItemInHand(hand);
                        itemSkin = ItemSkins.getItemSkin(stack.getItem());
                    }
                    TrailInfo trailInfo = tkkTrail.buildTrailInfo(itemSkin);
                    if(itemSkin!=null){trailInfo=itemSkin.trailInfo().overwrite(trailInfo);}

                    if (trailInfo.playable()) {
                        ((LivingEntity) entitypatch.getOriginal()).level().addParticle(TkkParticles.SWING_TRAIL.get(), eid, 0.0D, animid, jointId, index, 0.0D);
                    }


                }
                idx = 0;
                for (ParticleTrail tkkTrail : epicAddCap.getTkkCustomTrail().readyParticleTrails) {
                    double eid = Double.longBitsToDouble((long) ((LivingEntity) entitypatch.getOriginal()).getId());
                    double animid = Double.longBitsToDouble((long) this.animationId);
                    double jointId = Double.longBitsToDouble((long) this.armature.searchJointByName(tkkTrail.joint).getId());
                    double index = Double.longBitsToDouble((long) (idx++));
                    ((LivingEntity) entitypatch.getOriginal()).level().addParticle(TkkParticles.PARTICLE_TRAIL.get(), eid, 0.0D, animid, jointId, index, 0.0D);



                }
            }
        }
        if(doVanilla) {
            this.getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent((trailInfos) -> {
                int idx = 0;
                Iterator var4 = trailInfos.iterator();
                while (var4.hasNext()) {
                    TrailInfo trailInfo = (TrailInfo) var4.next();
                    double eid = Double.longBitsToDouble((long) ((LivingEntity) entitypatch.getOriginal()).getId());
                    double animid = Double.longBitsToDouble((long) this.animationId);
                    double jointId = Double.longBitsToDouble((long) this.armature.searchJointByName(trailInfo.joint).getId());
                    double index = Double.longBitsToDouble((long) (idx++));
                    if (trailInfo.hand != null) {
                        ItemStack stack = ((LivingEntity) entitypatch.getOriginal()).getItemInHand(trailInfo.hand);
                        ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());
                        if (itemSkin != null) {
                            trailInfo = itemSkin.trailInfo().overwrite(trailInfo);
                        }
                    }

                    if (trailInfo.playable()) {
                        ((LivingEntity) entitypatch.getOriginal()).level().addParticle(trailInfo.particle, eid, 0.0D, animid, jointId, index, 0.0D);
                    }
                }

            });
        }

    }

    @Inject(method = "Lyesman/epicfight/api/animation/types/StaticAnimation;end(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/DynamicAnimation;Z)V",at = @At("HEAD"))
    private void onEnd(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd, CallbackInfo callbackInfo) {

        MinecraftForge.EVENT_BUS.post(new AnimationEndEvent((StaticAnimation) (Object)this,entitypatch.getOriginal(),entitypatch,nextAnimation,isEnd));
        //if(entitypatch instanceof PlayerPatch){TkkEpic.getInstance().broadcast("clear Trail");}
        //epicAddCap.getTkkCustomTrail().readyAnimation();
    }


}
