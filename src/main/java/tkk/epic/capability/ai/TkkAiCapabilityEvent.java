package tkk.epic.capability.ai;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.CapabilityEventLoader;
import tkk.epic.capability.ai.module.TkkAiModuleEventName;
import tkk.epic.capability.ai.module.TkkAiModuleManager;
import tkk.epic.capability.epicAdd.EpicAddCapabilityEvent;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.capability.tkk.TkkCapabilityEvent;
import tkk.epic.event.AttackResultEvent;
import tkk.epic.event.TryHurtEvent;
import tkk.epic.skill.SkillContainer;
import tkk.epic.skill.SkillManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import static tkk.epic.skill.SkillManager.identifier.containerSelf;

public class TkkAiCapabilityEvent {
    public static void reg(IEventBus bus){
        bus.addListener(TkkAiCapabilityEvent::doCommandStuff);
        bus.addListener(TkkAiModuleManager::createRegistry);

    }
    public static void doCommandStuff(FMLCommonSetupEvent event){
        MinecraftForge.EVENT_BUS.register(TkkAiCapabilityEvent.class);
    }
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITkkAiCapability.class);
    }
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player){return;}
        if((event.getObject() instanceof LivingEntity) && event.getObject().getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null) == null) {
            event.addCapability(new ResourceLocation(TkkEpic.MODID, "tkk_ai_data"), new TkkAiCapabilityProvider((LivingEntity) event.getObject()));
        }
    }

    public static void runAiEvent(Entity entity,String event,Object... args){
        ITkkAiCapability cap=entity.getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
        if (cap==null){return;}
        cap.runEvent(event,args);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onJoinEn(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide()){return;}
        if(!(event.getEntity() instanceof LivingEntity)){return;}
        ITkkAiCapability cap=event.getEntity().getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
        if (cap==null){return;}
        if(event.getEntity() instanceof Mob && !((Mob) event.getEntity()).isNoAi()){
            ((Mob) event.getEntity()).goalSelector.addGoal(0,new TkkAiTickEventGoal(cap));
        }
        cap.runEvent(TkkAiModuleEventName.ENTITY_JOIN_LEVEL_EVENT,new Object[]{event});
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void equipChangeEvent(LivingEquipmentChangeEvent event) {
        if(event.getEntity().level().isClientSide()){return;}
        ITkkAiCapability cap=event.getEntity().getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
        if (cap==null){return;}
        cap.runEvent(TkkAiModuleEventName.LIVING_EQUIPMENT_CHANGE_EVENT,new Object[]{event});
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void mountEvent(EntityMountEvent event) {
        if(event.getEntityMounting().level().isClientSide()){return;}
        ITkkAiCapability cap=event.getEntityMounting().getCapability(TkkAiCapabilityProvider.TKK_AI_CAPABILITY).orElse(null);
        if (cap==null){return;}
        cap.runEvent(TkkAiModuleEventName.ENTITY_MOUNT_EVENT,new Object[]{event});
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onAttack(LivingAttackEvent event){
        if(event.isCanceled()){return;}
        runAiEvent(event.getEntity(),TkkAiModuleEventName.BE_ATTACK,event);
        if(event.getSource().getEntity()!=null){runAiEvent(event.getSource().getEntity(),TkkAiModuleEventName.ON_ATTACK,event);}
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event){
        if(event.isCanceled()){return;}
        runAiEvent(event.getEntity(),TkkAiModuleEventName.BE_HURT,event);
        if(event.getSource().getEntity()!=null){runAiEvent(event.getSource().getEntity(),TkkAiModuleEventName.ON_HURT,event);}
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void tryHurt(TryHurtEvent event){
        if(event.isCanceled()){return;}
        runAiEvent(event.patch.getOriginal(),TkkAiModuleEventName.BE_TRY_HURT,event);
        if(event.getAttacker()!=null){runAiEvent(event.getAttacker(),TkkAiModuleEventName.ON_TRY_HURT,event);}
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void onDeath(LivingDeathEvent event){
        if(event.isCanceled()){return;}
        runAiEvent(event.getEntity(),TkkAiModuleEventName.BE_DEATH,event);
        if(event.getSource().getEntity()!=null){runAiEvent(event.getSource().getEntity(),TkkAiModuleEventName.ON_DEATH,event);}
    }
    @SubscribeEvent(priority= EventPriority.HIGH)
    public static void attackResult(AttackResultEvent event){
        runAiEvent(event.target,TkkAiModuleEventName.BE_ATTACK_RESULT,event);
        if(event.patch.getOriginal()!=null){runAiEvent(event.patch.getOriginal(),TkkAiModuleEventName.ON_ATTACK_RESULT,event);}

    }
}
