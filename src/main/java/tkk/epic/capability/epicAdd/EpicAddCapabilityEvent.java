package tkk.epic.capability.epicAdd;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.animationTemplate.AnimationTemplateEvent;
import tkk.epic.capability.epicAdd.customStateSpectrum.StatesTemplateManager;
import tkk.epic.event.AnimationEndEvent;
import yesman.epicfight.api.animation.types.AttackAnimation;

public class EpicAddCapabilityEvent {
    public static void reg(){
        StatesTemplateManager.reg();
        MinecraftForge.EVENT_BUS.register(EpicAddCapabilityEvent.class);
        MinecraftForge.EVENT_BUS.register(AnimationTemplateEvent.class);
    }
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IEpicAddCapability.class);
    }
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof LivingEntity && event.getObject().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null) == null) {
            event.addCapability(new ResourceLocation(TkkEpic.MODID, "tkk_epic_add"), new EpicAddCapabilityProvider((LivingEntity) event.getObject()));
        }
    }
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event) {
        //event.getOriginal().reviveCaps();

        IEpicAddCapability oldEpicAddCap = event.getOriginal().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);

        if (oldEpicAddCap != null) {
            IEpicAddCapability newCap = event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
            //newCap.deserializeNBT(oldCap.serializeNBT());
        }
        //event.getOriginal().invalidateCaps();
    }
    //仅逻辑端,不需要同步
    /*
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if(event.getTarget().getCapability(EnrCapabilityProvider.TKK_CAPABILITY,null).orElse(null)!=null){
            TkkEpic.LOGGER.log(Level.ERROR,"2kk2 onStartTracking同步");
        }
    }
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getPlayer().getCapability(EnrCapabilityProvider.TKK_CAPABILITY,null).orElse(null)!=null){
            TkkEpic.LOGGER.log(Level.ERROR,"2kk2 onPlayerLoggedIn同步");

        }
    }

     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void animationEndEvent(AnimationEndEvent event){
        IEpicAddCapability cap = event.getEntity().getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null);
        if (cap==null){return;}
        if(event.animation instanceof AttackAnimation){

            if (event.getEntity().tickCount!=cap.getAttackSpeedObject().updateTick){cap.clearModifiersAttackSpeed();}
            if(event.getEntity().tickCount!=cap.getCDS().updateTick){cap.getCDS().clear();};
            if(event.getEntity().tickCount!=cap.getCustomStateSpectrum().updateTick){cap.getCustomStateSpectrum().clearTimePairs();};

        }
    }
}
