package tkk.epic.capability.epicAdd.animationTemplate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.event.AnimationEndEvent;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.TryHurtEvent;

import static tkk.epic.skill.SkillManager.identifier.containerSelf;

public class AnimationTemplateEvent {


    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAnimationEnd(AnimationEndEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        IEpicAddCapability epicAdd = event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd == null) {
            return;
        }
        AnimationTemplateManager animationTemplateManager = epicAdd.getAnimationTemplateManager();
        AnimationTemplate animationTemplate = animationTemplateManager.getNow();
        if (animationTemplate == null) {
            return;
        }
        if (!animationTemplateManager.playTick) {
            animationTemplate.preEnd(event.getEntity(), !event.isEnd);
            epicAdd.getAnimationTemplateManager().setNow(null);
            animationTemplate.postEnd(event.getEntity(), !event.isEnd);
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onTick(LivingEvent.LivingTickEvent event){
        if(event.getEntity().level().isClientSide()){return;}
        IEpicAddCapability epicAdd=event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd==null){return;}
        AnimationTemplateManager animationTemplateManager=epicAdd.getAnimationTemplateManager();
        AnimationTemplate animationTemplate=animationTemplateManager.getNow();
        if (animationTemplate==null){return;}
        if (animationTemplate.isEnd(event.getEntity())){
            animationTemplate.preEnd(event.getEntity(),false);
            epicAdd.getAnimationTemplateManager().setNow(null);
            animationTemplate.postEnd(event.getEntity(), false);
        }else{
            animationTemplate.tick(event.getEntity());
        }
        if (epicAdd.getAnimationTemplateManager().playTick){
            epicAdd.getAnimationTemplateManager().playTick=false;
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttack(LivingAttackEvent event){
        if(event.isCanceled()){return;}
        IEpicAddCapability epicAdd=event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd!=null && epicAdd.getAnimationTemplateManager().getNow()!=null){
            epicAdd.getAnimationTemplateManager().getNow().beAttack(event.getEntity(), event);
        }
        if(event.getSource().getEntity()!=null) {
            epicAdd = event.getSource().getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
                epicAdd.getAnimationTemplateManager().getNow().onAttack((LivingEntity) event.getSource().getEntity(), event);
            }
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event){
        if(event.isCanceled()){return;}
        IEpicAddCapability epicAdd=event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd!=null && epicAdd.getAnimationTemplateManager().getNow()!=null){
            epicAdd.getAnimationTemplateManager().getNow().beHurt(event.getEntity(), event);
        }
        if(event.getSource().getEntity()!=null) {
            epicAdd = event.getSource().getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
                epicAdd.getAnimationTemplateManager().getNow().onHurt((LivingEntity) event.getSource().getEntity(), event);
            }
        }
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void attackResult(AttackResultEvent event){
        if(event.target instanceof LivingEntity) {
            IEpicAddCapability epicAdd = event.target.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
                epicAdd.getAnimationTemplateManager().getNow().beAttackResult((LivingEntity) event.target, event);
            }
        }
        IEpicAddCapability epicAdd = event.patch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
            epicAdd.getAnimationTemplateManager().getNow().onAttackResult((LivingEntity) event.patch.getOriginal(), event);
        }

    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void tryHurt(TryHurtEvent event){
        if(event.isCanceled()){return;}
        if(event.patch.getOriginal() instanceof LivingEntity){
            IEpicAddCapability epicAdd = event.patch.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
                epicAdd.getAnimationTemplateManager().getNow().beTryHurt((LivingEntity) event.patch.getOriginal(), event);
            }
        }
        if(event.getAttacker()!=null && event.getAttacker() instanceof LivingEntity){
            IEpicAddCapability epicAdd = event.getAttacker().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            if (epicAdd != null && epicAdd.getAnimationTemplateManager().getNow() != null) {
                epicAdd.getAnimationTemplateManager().getNow().onTryHurt((LivingEntity) event.getAttacker(), event);
            }
        }
    }



}
