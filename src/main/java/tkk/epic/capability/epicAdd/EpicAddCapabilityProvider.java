package tkk.epic.capability.epicAdd;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EpicAddCapabilityProvider implements ICapabilityProvider {
    //@CapabilityInject(ITkkCapability.class)
    public static final Capability<IEpicAddCapability> EPIC_ADD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public IEpicAddCapability cap;
    public EpicAddCapabilityProvider(LivingEntity p){
        cap=new EpicAddCapability(p);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY) {
            return LazyOptional.of(() -> cap).cast();
        }
        return LazyOptional.empty();
    }

}
