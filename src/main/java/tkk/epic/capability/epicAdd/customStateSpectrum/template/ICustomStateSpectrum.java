package tkk.epic.capability.epicAdd.customStateSpectrum.template;

import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;
import java.util.Set;

public interface ICustomStateSpectrum {
    Set<Map.Entry<EntityState.StateFactor<?>, Object>> getStates(LivingEntityPatch<?> entitypatch);

    void removeState(EntityState.StateFactor<?> state);

    boolean hasState(EntityState.StateFactor<?> state);

    boolean isIn(LivingEntityPatch<?> entitypatch, float time);

    void writeBuf(FriendlyByteBuf buf);
    void readBuf(FriendlyByteBuf buf);
}
