package tkk.epic.capability.epicAdd.customStateSpectrum;

import com.google.common.collect.Sets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.CustomSimpleStates;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.ICustomStateSpectrum;
import tkk.epic.network.SPEpicAddAttackSpeedUpdata;
import tkk.epic.network.SPEpicCustomStatesSpectrumUpdata;
import tkk.epic.network.TkkEpicNetworkManager;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StateSpectrum;
import yesman.epicfight.api.utils.datastruct.TypeFlexibleHashMap;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomStateSpectrum {
    public final LivingEntity entity;
    public final Set<ICustomStateSpectrum> timePairs = Sets.newHashSet();
    /**If it is the same as the current tick, it is not cleared at the end of the action*/
    public int updateTick=0;

    public CustomStateSpectrum(LivingEntity entity){
        this.entity=entity;
    }
    public void clearTimePairs(){
        timePairs.clear();
    }
    public void addSimpleState(float start,float end,String stateFactor,Object value){
        CustomSimpleStates timePair = null;
        for (ICustomStateSpectrum stateSpectrum:timePairs){
            if (!(stateSpectrum instanceof CustomSimpleStates)){continue;}
            if (((CustomSimpleStates) stateSpectrum).start==start && ((CustomSimpleStates) stateSpectrum).end==end){
                timePair= (CustomSimpleStates) stateSpectrum;
                break;
            }
        }
        if (timePair==null){
            timePair=new CustomSimpleStates(start,end);
            timePairs.add(timePair);
        }
        timePair.addState(StatesTemplateManager.getStateFactorForId(stateFactor),value);
    }


    public <T> T getSingleState(T t,EntityState.StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
        if (timePairs.isEmpty()){return t;}
        for (ICustomStateSpectrum state : this.timePairs) {
            if (state.isIn(entitypatch, time)) {
                for (Map.Entry<EntityState.StateFactor<?>, ?> timeEntry : state.getStates(entitypatch)) {
                    if (timeEntry.getKey() == stateFactor) {
                        return (T)timeEntry.getValue();
                    }
                }
            }
        }

        return t;
    }

    public TypeFlexibleHashMap<EntityState.StateFactor<?>> getStateMap(TypeFlexibleHashMap<EntityState.StateFactor<?>> stateMap,LivingEntityPatch<?> entitypatch, float time) {
        //TypeFlexibleHashMap<EntityState.StateFactor<?>> stateMap = new TypeFlexibleHashMap<>(true);
        if (timePairs.isEmpty()){return stateMap;}

        for (ICustomStateSpectrum state : this.timePairs) {
            if (state.isIn(entitypatch, time)) {
                for (Map.Entry<EntityState.StateFactor<?>, ?> timeEntry : state.getStates(entitypatch)) {
                    stateMap.put(timeEntry.getKey(), timeEntry.getValue());
                }
            }
        }

        return stateMap;
    }

    public void updataCustomStateSpectrum() {
        updateTick=this.entity.tickCount;
        SPEpicCustomStatesSpectrumUpdata packet=new SPEpicCustomStatesSpectrumUpdata(this.entity.getId(),this);
        TkkEpicNetworkManager.sendToAllPlayerTrackingThisEntity(packet,this.entity);
        if(entity instanceof ServerPlayer){
            TkkEpicNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);
        }
    }


}
