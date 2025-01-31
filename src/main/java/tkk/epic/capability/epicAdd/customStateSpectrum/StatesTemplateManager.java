package tkk.epic.capability.epicAdd.customStateSpectrum;

import net.minecraft.network.FriendlyByteBuf;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.CustomSimpleStates;
import tkk.epic.capability.epicAdd.customStateSpectrum.template.ICustomStateSpectrum;
import yesman.epicfight.api.animation.types.EntityState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StatesTemplateManager {
    public static void reg(){
        StatesTemplateManager.regStateFactor();
        StatesTemplateManager.regStateSpectrum();
    }




    public static void regStateSpectrum(){
        registerStateSpectrum(stateSpectrumId++, CustomSimpleStates.class);
    }
    public static int stateSpectrumId=0;
    public static final Map<Integer, Class> templateId=new HashMap<>();

    public static void registerStateSpectrum(int id,Class dass){
        templateId.put(id,dass);
    }

    public static ICustomStateSpectrum createStateSpectrumForId(int id){
        try {
            return (ICustomStateSpectrum) templateId.get(id).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getStateSpectrumId(ICustomStateSpectrum stateSpectrum){
        for (int i:templateId.keySet()){
            if(templateId.get(i)==stateSpectrum.getClass()){
                return i;
            }
        }
        return 0;
    }

    public static void writeStateSpectrum(FriendlyByteBuf buf, ICustomStateSpectrum stateSpectrum){
        buf.writeInt(StatesTemplateManager.getStateSpectrumId(stateSpectrum));
        stateSpectrum.writeBuf(buf);
    }
    public static ICustomStateSpectrum readStateSpectrum(FriendlyByteBuf buf){
        ICustomStateSpectrum temp=StatesTemplateManager.createStateSpectrumForId(buf.readInt());
        temp.readBuf(buf);
        return temp;
    }











    public static final Map<String,EntityState.StateFactor<?>> stateFactorId=new HashMap<>();
    public static final Map<EntityState.StateFactor<?>, BiConsumer<FriendlyByteBuf,Object>> stateFactorWriteValue=new HashMap<>();
    public static final Map<EntityState.StateFactor<?>, Function<FriendlyByteBuf,Object>> stateFactorReadValue=new HashMap<>();



    public static void regStateFactor(){
        registerStateFactor(EntityState.TURNING_LOCKED,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.MOVEMENT_LOCKED,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.ATTACKING,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.CAN_BASIC_ATTACK,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.CAN_SKILL_EXECUTION,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.INACTION,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.KNOCKDOWN,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.LOCKON_ROTATE,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.UPDATE_LIVING_MOTION,(buf,v)->{buf.writeBoolean((Boolean) v);},(buf)->{return buf.readBoolean();});
        registerStateFactor(EntityState.HURT_LEVEL,(buf,v)->{buf.writeInt((Integer) v);},(buf)->{return buf.readInt();});
        registerStateFactor(EntityState.PHASE_LEVEL,(buf,v)->{buf.writeInt((Integer) v);},(buf)->{return buf.readInt();});
    }

    public static void registerStateFactor(EntityState.StateFactor<?> stateFactor,BiConsumer<FriendlyByteBuf,Object> write,Function<FriendlyByteBuf,Object> read){
        stateFactorId.put(stateFactor.toString(),stateFactor);
        stateFactorWriteValue.put(stateFactor,write);
        stateFactorReadValue.put(stateFactor,read);
    }

    public static EntityState.StateFactor<?> getStateFactorForId(String id){
        return stateFactorId.get(id);
    }

    public static String getStateFactorId(EntityState.StateFactor<?> stateFactor){
        for (String i:stateFactorId.keySet()){
            if(stateFactorId.get(i)==stateFactor){
                return i;
            }
        }
        return null;
    }

    //write stateFactorId value
    public static void writeStateFactor(FriendlyByteBuf buf, EntityState.StateFactor<?> stateFactor, Object value) {
        buf.writeUtf(StatesTemplateManager.getStateFactorId(stateFactor));
        stateFactorWriteValue.get(stateFactor).accept(buf,value);
    }
    //read stateFactorId
    public static EntityState.StateFactor<?> getStateFactorFromBuf(FriendlyByteBuf buf) {
        return StatesTemplateManager.getStateFactorForId(buf.readUtf());
    }
    //read value
    public static Object getStateFactorValueFromBuf(FriendlyByteBuf buf, EntityState.StateFactor<?> stateFactor) {
        return stateFactorReadValue.get(stateFactor).apply(buf);
    }




}
