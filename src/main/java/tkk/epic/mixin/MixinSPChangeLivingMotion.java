package tkk.epic.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Mixin(value = SPChangeLivingMotion.class,remap = true)
public abstract class MixinSPChangeLivingMotion {
    @Shadow private int entityId;
    @Shadow private int count;
    @Shadow private boolean setChangesAsDefault;
    @Shadow private List<LivingMotion> motionList;
    @Shadow private List<StaticAnimation> animationList;

    /**
     * @author
     */
    @Overwrite
    public static void handle(SPChangeLivingMotion msg, Supplier<NetworkEvent.Context> ctx) {
        ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.player.level().getEntity(((MixinSPChangeLivingMotion)(Object) msg).entityId);
            if (entity != null) {
                EntityPatch patch = entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
                IEpicAddCapability epicAdd=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
                if (patch instanceof LivingEntityPatch) {
                    LivingEntityPatch<?> entitypatch = (LivingEntityPatch)patch;
                    ClientAnimator animator = entitypatch.getClientAnimator();
                    animator.resetLivingAnimations();
                    animator.offAllLayers();
                    animator.resetMotion();
                    animator.resetCompositeMotion();

                    if(epicAdd==null) {
                        entitypatch.getClientAnimator().addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD);
                        animator.setCurrentMotionsAsDefault();

                        int count = ((MixinSPChangeLivingMotion) (Object) msg).count;
                        for (int i = 0; i < count; ++i) {
                            entitypatch.getClientAnimator().addLivingAnimation((LivingMotion) ((MixinSPChangeLivingMotion) (Object) msg).motionList.get(i), (StaticAnimation) ((MixinSPChangeLivingMotion) (Object) msg).animationList.get(i));
                        }

                        if (((MixinSPChangeLivingMotion) (Object) msg).setChangesAsDefault) {
                            animator.setCurrentMotionsAsDefault();
                        }

                        //put
                        entitypatch.getClientAnimator().addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD);
                    }else{
                        if (((MixinSPChangeLivingMotion) (Object) msg).setChangesAsDefault){
                            int count = ((MixinSPChangeLivingMotion) (Object) msg).count;
                            for (int i = 0; i < count; ++i) {
                                entitypatch.getClientAnimator().addLivingAnimation((LivingMotion) ((MixinSPChangeLivingMotion) (Object) msg).motionList.get(i), (StaticAnimation) ((MixinSPChangeLivingMotion) (Object) msg).animationList.get(i));
                            }

                            Iterator<LivingMotion> iterator=epicAdd.getCustomMotionAnimation().defaultLivingAnimations.keySet().iterator();
                            while (iterator.hasNext()){
                                LivingMotion temp=iterator.next();
                                entitypatch.getClientAnimator().addLivingAnimation(temp,epicAdd.getCustomMotionAnimation().defaultLivingAnimations.get(temp));
                            }

                            animator.setCurrentMotionsAsDefault();

                            iterator=epicAdd.getCustomMotionAnimation().livingAnimations.keySet().iterator();
                            while (iterator.hasNext()){
                                LivingMotion temp=iterator.next();
                                entitypatch.getClientAnimator().addLivingAnimation(temp,epicAdd.getCustomMotionAnimation().livingAnimations.get(temp));
                            }


                        }else{

                            Iterator<LivingMotion> iterator=epicAdd.getCustomMotionAnimation().defaultLivingAnimations.keySet().iterator();
                            while (iterator.hasNext()){
                                LivingMotion temp=iterator.next();
                                entitypatch.getClientAnimator().addLivingAnimation(temp,epicAdd.getCustomMotionAnimation().defaultLivingAnimations.get(temp));
                            }

                            animator.setCurrentMotionsAsDefault();

                            int count = ((MixinSPChangeLivingMotion) (Object) msg).count;
                            for (int i = 0; i < count; ++i) {
                                entitypatch.getClientAnimator().addLivingAnimation((LivingMotion) ((MixinSPChangeLivingMotion) (Object) msg).motionList.get(i), (StaticAnimation) ((MixinSPChangeLivingMotion) (Object) msg).animationList.get(i));
                            }

                            iterator=epicAdd.getCustomMotionAnimation().livingAnimations.keySet().iterator();
                            while (iterator.hasNext()){
                                LivingMotion temp=iterator.next();
                                entitypatch.getClientAnimator().addLivingAnimation(temp,epicAdd.getCustomMotionAnimation().livingAnimations.get(temp));
                            }

                        }
                    }
                }
            }

        });
        ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }
}
