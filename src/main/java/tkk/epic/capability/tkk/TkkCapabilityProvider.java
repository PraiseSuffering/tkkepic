package tkk.epic.capability.tkk;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TkkCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    //@CapabilityInject(ITkkCapability.class)
    public static final Capability<ITkkCapability> TKK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public ITkkCapability cap;
    public TkkCapabilityProvider(ServerPlayer p){
        cap=new TkkCapability(p);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == TkkCapabilityProvider.TKK_CAPABILITY) {
            return LazyOptional.of(() -> cap).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return cap.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        cap.deserializeNBT(nbt);
    }
}
