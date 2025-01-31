package tkk.epic.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tkk.epic.TkkEpic;
import tkk.epic.particle.TkkParticles;
import tkk.epic.particle.client.TkkTrail;
import tkk.epic.particle.client.TkkTrailParticle;
import tkk.epic.particle.client.VanillaTrailParticle;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= TkkEpic.MODID, value=Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ClientBusEvent {
    @SubscribeEvent()
    public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TkkParticles.SWING_TRAIL.get(), TkkTrail.Provider::new);
        event.registerSpriteSet(TkkParticles.PARTICLE_TRAIL.get(), TkkTrailParticle.Provider::new);
        event.registerSpriteSet(TkkParticles.VANILLA_PARTICLE_TRAIL.get(), VanillaTrailParticle.Provider::new);
    }
}
