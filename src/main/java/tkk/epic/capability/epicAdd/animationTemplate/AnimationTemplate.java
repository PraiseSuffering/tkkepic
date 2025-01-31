package tkk.epic.capability.epicAdd.animationTemplate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.event.AnimationEndEvent;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.TryHurtEvent;
import tkk.epic.mixin.MixinServerAnimator;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


public class AnimationTemplate {
    public StaticAnimation animation;
    public float convertTimeModifier;
    public final HashMap data=new HashMap();
    public final subscribe preEndFN=new subscribe();
    public final subscribe postEndFN=new subscribe();
    public final subscribe tickFN=new subscribe();
    public final subscribe prePlayFN=new subscribe();
    public final subscribe postPlayFN=new subscribe();
    public final subscribe onHurtFN=new subscribe();
    public final subscribe beHurtFN=new subscribe();
    public final subscribe onAttackFN=new subscribe();
    public final subscribe beAttackFN=new subscribe();
    public final subscribe onAttackResultFN=new subscribe();
    public final subscribe beAttackResultFN=new subscribe();
    public final subscribe onTryHurtFN=new subscribe();
    public final subscribe beTryHurtFN=new subscribe();



    public AnimationTemplate(StaticAnimation animation, float convertTimeModifier){
        this.animation=animation;
        this.convertTimeModifier=convertTimeModifier;
    }

    public boolean isAnimationTemplate(LivingEntity entity) {
        IEpicAddCapability epicAdd=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd==null){return false;}
        AnimationTemplateManager animationTemplateManager=epicAdd.getAnimationTemplateManager();
        AnimationTemplate animationTemplate=animationTemplateManager.getNow();
        if (animationTemplate==null){return !this.isEnd(entity);}
        return animationTemplate == this && !this.isEnd(entity);
    }
    public boolean isEnd(LivingEntity entity) {
        if (animation==null){return true;}
        EntityPatch entityPatch = entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if (!(entityPatch instanceof LivingEntityPatch)){
            return true;
        }
        int nowAnimation=((LivingEntityPatch<?>) entityPatch).getServerAnimator().getPlayerFor(null).getAnimation().getId();
        if(nowAnimation==-1 && ((MixinServerAnimator) ((LivingEntityPatch<?>) entityPatch).getServerAnimator()).getNextPlaying()!=null){
            nowAnimation=((MixinServerAnimator) ((LivingEntityPatch<?>) entityPatch).getServerAnimator()).getNextPlaying().getId();
        }
        return nowAnimation!=animation.getId();
    }
    public boolean onAnimationEnd(LivingEntity entity, AnimationEndEvent event){
        IEpicAddCapability epicAdd = event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd == null) {
            return true;
        }
        AnimationTemplateManager animationTemplateManager = epicAdd.getAnimationTemplateManager();
        if (animationTemplateManager.playTick){
            return event.nextAnimation.getId()!= animation.getId();
        }else{
            return event.animation.getId()==animation.getId();
        }
    }
    public void play(LivingEntity entity){
        IEpicAddCapability epicAdd=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd==null){return;}
        AnimationTemplate animationTemplate=epicAdd.getAnimationTemplateManager().getNow();
        if (animationTemplate!=null){
            animationTemplate.preEnd(entity,true);
        }
        epicAdd.getAnimationTemplateManager().playTick=true;
        epicAdd.getAnimationTemplateManager().setNow(this);
        if (animationTemplate!=null){
            animationTemplate.postEnd(entity,true);
        }
        //do pre
        prePlay(entity);

        if (animation!=null){
            EntityPatch entityPatch = entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
            if (entityPatch instanceof LivingEntityPatch){
                ((LivingEntityPatch<?>) entityPatch).playAnimationSynchronized(animation,convertTimeModifier);
            }
        }
        //do post
        postPlay(entity);
    }

    public HashMap getEntityData(LivingEntity entity){
        IEpicAddCapability epicAdd=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd==null){return null;}

        return epicAdd.getAnimationTemplateManager().data;
    };

    public void preEnd(LivingEntity entity,boolean isBreak) {
        preEndFN.accept(new Object[]{this,entity,isBreak});
    }
    public void postEnd(LivingEntity entity,boolean isBreak) {
        postEndFN.accept(new Object[]{this,entity,isBreak});
    }

    public void tick(LivingEntity entity) {
        tickFN.accept(new Object[]{this,entity});
    }

    public void prePlay(LivingEntity entity){
        prePlayFN.accept(new Object[]{this,entity});
    }
    public void postPlay(LivingEntity entity){
        postPlayFN.accept(new Object[]{this,entity});
    }

    public void onHurt(LivingEntity entity, LivingHurtEvent event) {
        onHurtFN.accept(new Object[]{this,entity,event});

    }
    public void beHurt(LivingEntity entity, LivingHurtEvent event) {
        beHurtFN.accept(new Object[]{this,entity,event});
    }
    public void onAttack(LivingEntity entity, LivingAttackEvent event) {
        onAttackFN.accept(new Object[]{this,entity,event});
    }
    public void beAttack(LivingEntity entity, LivingAttackEvent event) {
        beAttackFN.accept(new Object[]{this,entity,event});
    }
    public void onAttackResult(LivingEntity entity, AttackResultEvent event) {
        onAttackResultFN.accept(new Object[]{this,entity,event});
    }
    public void beAttackResult(LivingEntity entity, AttackResultEvent event) {
        beAttackResultFN.accept(new Object[]{this,entity,event});
    }
    public void onTryHurt(LivingEntity entity, TryHurtEvent event) {
        onTryHurtFN.accept(new Object[]{this,entity,event});
    }
    public void beTryHurt(LivingEntity entity, TryHurtEvent event) {
        beTryHurtFN.accept(new Object[]{this,entity,event});
    }


    public class subscribe{
        public final ArrayList<Consumer<Object[]>> highest=new ArrayList<>();
        public final ArrayList<Consumer<Object[]>> high=new ArrayList<>();
        public final ArrayList<Consumer<Object[]>> normal=new ArrayList<>();
        public final ArrayList<Consumer<Object[]>> low=new ArrayList<>();
        public final ArrayList<Consumer<Object[]>> lowest=new ArrayList<>();

        public subscribe(){}

        public void accept(Object[] args){
            try {
                for (Consumer<Object[]> c : highest) {
                    c.accept(args);
                }
                for (Consumer<Object[]> c : high) {
                    c.accept(args);
                }
                for (Consumer<Object[]> c : normal) {
                    c.accept(args);
                }
                for (Consumer<Object[]> c : low) {
                    c.accept(args);
                }
                for (Consumer<Object[]> c : lowest) {
                    c.accept(args);
                }
            }catch (Throwable e){
                e.printStackTrace();
                TkkEpic.getInstance().broadcast("§cAnimationTemplate error:§f "+e);
            }
        }



    }
}
