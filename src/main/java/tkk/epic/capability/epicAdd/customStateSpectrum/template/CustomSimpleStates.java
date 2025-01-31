package tkk.epic.capability.epicAdd.customStateSpectrum.template;

import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import tkk.epic.capability.epicAdd.customStateSpectrum.StatesTemplateManager;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;
import java.util.Set;

public class CustomSimpleStates implements ICustomStateSpectrum {
    public float start;
    public float end;
    public Map<EntityState.StateFactor<?>, Object> states = Maps.newHashMap();

    public CustomSimpleStates(float start, float end) {
        this.start = start;
        this.end = end;
    }
    public CustomSimpleStates() {
    }


    @Override
    public boolean isIn(LivingEntityPatch<?> entitypatch, float time) {
        return this.start <= time && this.end > time;
    }

    public CustomSimpleStates addState(EntityState.StateFactor<?> factor, Object val) {
        this.states.put(factor, val);
        return this;
    }

    @Override
    public Set<Map.Entry<EntityState.StateFactor<?>, Object>> getStates(LivingEntityPatch<?> entitypatch) {
        return this.states.entrySet();
    }

    @Override
    public boolean hasState(EntityState.StateFactor<?> state) {
        return this.states.containsKey(state);
    }

    @Override
    public void removeState(EntityState.StateFactor<?> state) {
        this.states.remove(state);
    }

    @Override
    public void writeBuf(FriendlyByteBuf buf){
        buf.writeFloat(start);
        buf.writeFloat(end);
        buf.writeInt(states.size());
        for (EntityState.StateFactor<?> stateFactor: states.keySet()){
            StatesTemplateManager.writeStateFactor(buf,stateFactor,states.get(stateFactor));
        }
    }
    @Override
    public void readBuf(FriendlyByteBuf buf){
        start=buf.readFloat();
        end=buf.readFloat();
        int size=buf.readInt();
        for(int i=0;i<size;i++){
            EntityState.StateFactor<?> stateFactor=StatesTemplateManager.getStateFactorFromBuf(buf);
            Object value=StatesTemplateManager.getStateFactorValueFromBuf(buf,stateFactor);
            addState(stateFactor,value);
        }

    }

    @Override
    public String toString() {
        return String.format("Time: %.2f ~ %.2f, States: %s", this.start, this.end, this.states);
    }
}
