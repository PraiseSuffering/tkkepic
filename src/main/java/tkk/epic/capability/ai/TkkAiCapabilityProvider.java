package tkk.epic.capability.ai;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import tkk.epic.capability.tkk.ITkkCapability;
import tkk.epic.capability.tkk.TkkCapability;
import tkk.epic.capability.tkk.TkkCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TkkAiCapabilityProvider  implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<ITkkAiCapability> TKK_AI_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public ITkkAiCapability cap;
    public TkkAiCapabilityProvider(LivingEntity p){
        cap=new TkkAiCapability(p);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == TkkAiCapabilityProvider.TKK_AI_CAPABILITY) {
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
