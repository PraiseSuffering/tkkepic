package tkk.epic.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.TkkAiCapabilityEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityEvent;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.IEpicAddCapability;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.capability.tkk.TkkCapabilityEvent;
import tkk.epic.capability.tkk.TkkCapabilityProvider;
import tkk.epic.network.TkkEpicNetworkManager;

public class CapabilityEventLoader {

    public static void reg(IEventBus bus){
        /*
        CapabilityManager.INSTANCE.register(ITkkCapability.class, new Capability<ITkkCapability>() {
            @Override
            public ITag writeNBT(Capability<ITkkCapability> capability, ITkkCapability instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ITkkCapability> capability, ITkkCapability instance, Direction side, ITag nbt) {
                instance.deserializeNBT((CompoundTag) nbt);
            }
        }, () -> null);

         */
        bus.addListener(CapabilityEventLoader::doCommandStuff);
        TkkAiCapabilityEvent.reg(bus);
    }
    public static void doCommandStuff(FMLCommonSetupEvent event){
        MinecraftForge.EVENT_BUS.register(TkkCapabilityEvent.class);
        EpicAddCapabilityEvent.reg();
    }
}
