package tkk.epic.capability.tkk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;

public class TkkCapabilityEvent {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITkkCapability.class);
    }
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof ServerPlayer)){return;}
        if(event.getObject().getCapability(TkkCapabilityProvider.TKK_CAPABILITY).orElse(null) == null) {
            event.addCapability(new ResourceLocation(TkkEpic.MODID, "tkk_data"), new TkkCapabilityProvider((ServerPlayer) event.getObject()));
        }
    }
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event) {
        //event.getOriginal().reviveCaps();
        ITkkCapability oldCap = (ITkkCapability)event.getOriginal().getCapability(TkkCapabilityProvider.TKK_CAPABILITY).orElse(null);

        if (oldCap != null) {
            ITkkCapability newCap = (ITkkCapability)event.getEntity().getCapability(TkkCapabilityProvider.TKK_CAPABILITY).orElse(null);
            newCap.deserializeNBT(oldCap.serializeNBT());
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
}
