package tkk.epic.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tkk.epic.TkkEpic;

public class TkkParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TkkEpic.MODID);

    public static final RegistryObject<SimpleParticleType> SWING_TRAIL = PARTICLES.register("swing_trail", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> PARTICLE_TRAIL = PARTICLES.register("particle_trail", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> VANILLA_PARTICLE_TRAIL = PARTICLES.register("vanilla_particle_trail", () -> new SimpleParticleType(true));


    public static void reg(IEventBus modBus){
        PARTICLES.register(modBus);
    }
}
