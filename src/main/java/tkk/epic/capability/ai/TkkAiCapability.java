package tkk.epic.capability.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import tkk.epic.TkkEpic;
import tkk.epic.capability.ai.module.TkkAiModule;
import tkk.epic.capability.ai.module.TkkAiModuleEventName;
import tkk.epic.capability.ai.module.TkkAiModuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TkkAiCapability implements ITkkAiCapability {
    public final static String MAIN_TEMP_DATA_KEY="MAIN_TEMP_DATA";
    public LivingEntity entity;
    public CompoundTag nbt;
    public ResourceLocation[] moduleIds;
    public HashMap tempData;

    public boolean enable;

    public List<TkkAiModule> modules;
    public TkkAiModule nowModule=null;
    public TkkAiCapability(LivingEntity entity){
        this.entity=entity;
        nbt=new CompoundTag();
        tempData=new HashMap();
        modules=new ArrayList<>();
        moduleIds=new ResourceLocation[0];
        enable=false;
    }

    @Override
    public void setModules(ResourceLocation[] moduleIds){
        this.moduleIds=moduleIds;
        nowModule=null;
        modules.clear();
        for (ResourceLocation id:moduleIds){
            TkkAiModule module= TkkAiModuleManager.getModule(id);
            if(module==null){continue;}
            modules.add(module);
        }
    }

    @Override
    public LivingEntity getEntity(){
        return this.entity;
    }
    @Override
    public CompoundTag getNbt() {
        return nbt;
    }

    @Override
    public HashMap getMainTempData() {
        return tempData;
    }

    @Override
    public HashMap getModuleTempData(ResourceLocation module) {
        if(tempData.containsKey(module) && !(tempData.get(module) instanceof HashMap)){
            tempData.remove(module);
            TkkEpic.LOGGER.log(Level.WARN,"[TkkAI] getModuleTempData !instanceof HashMap");
        }
        return (HashMap) tempData.computeIfAbsent(module,(key)->{return new HashMap<>();});
    }

    @Override
    public HashMap getTempData() {
        if(tempData.containsKey(MAIN_TEMP_DATA_KEY) && !(tempData.get(MAIN_TEMP_DATA_KEY) instanceof HashMap)){
            tempData.remove(MAIN_TEMP_DATA_KEY);
            TkkEpic.LOGGER.log(Level.WARN,"[TkkAI] getTempData !instanceof HashMap");
        }
        return (HashMap) tempData.computeIfAbsent(MAIN_TEMP_DATA_KEY,(key)->{return new HashMap<>();});
    }

    @Override
    public List<TkkAiModule> getModules() {
        return modules;
    }

    @Nullable
    @Override
    public TkkAiModule getNowModule() {
        return nowModule;
    }

    @Override
    public TkkAiModule getExpect(int expect) {

        int allRate=0;
        ArrayList<TkkAiModule> modules=new ArrayList<>();
        ArrayList<Integer> expects=new ArrayList<>();

        for (TkkAiModule module:this.modules){
            if(module==null){continue;}
            int moduleExpect=module.getExpect(Objects.equals(this.getNowModule(), module),this);
            if(moduleExpect-expect<=0){continue;}
            modules.add(module);
            expects.add(moduleExpect);
            allRate+=moduleExpect;
        }
        if (modules.size()==0){
            return null;
        }
        int randomRate= (int) (Math.floor(Math.random()*allRate)+1);
        int temp=0;
        for (int i=0;i<modules.size();i++){
            temp+=expects.get(i);
            if(temp>=randomRate){
                return modules.get(i);
            }
        }
        return null;
    }

    @Override
    public void updateExpect() {
        int nowExpect=0;
        TkkAiModule nowModule=this.getNowModule();
        if(nowModule!=null){
            nowExpect=nowModule.getExpect(true,this);
        }
        TkkAiModule switchModule=getExpect(nowExpect);
        if(switchModule==null){return;}
        if(nowModule!=null){
            nowModule.onDisable(this);
        }
        this.nowModule= switchModule;
        switchModule.onEnable(this);
    }

    @Override
    public boolean isEnable(){
        return this.enable;
    }
    @Override
    public void setEnable(boolean enable){
        if(enable){
            runEvent(TkkAiModuleEventName.AI_ENABLE,new Object[0]);
        }
        this.enable=enable;
    }

    @Override
    public void aiTick() {
        updateExpect();
        runEvent(TkkAiModuleEventName.AI_TICK,new Object[]{});

    }
    @Override
    public void runEvent(String event, Object[] args) {
        for (TkkAiModule module:this.modules){
            module.runEvent(Objects.equals(this.getNowModule(), module),this,event,args);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag=new CompoundTag();
        compoundTag.put("nbt",nbt);
        ListTag idList=new ListTag();
        for (ResourceLocation id:this.moduleIds){
            idList.add(StringTag.valueOf(id.toString()));
        }
        compoundTag.put("moduleIds",idList);
        compoundTag.putBoolean("enable",enable);
        runEvent(TkkAiModuleEventName.SERIALIZE_NBT,new Object[]{compoundTag});
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.nbt=nbt.getCompound("nbt");
        ListTag idList=nbt.getList("moduleIds",8);
        ResourceLocation[] moduleIds=new ResourceLocation[idList.size()];
        for (int i=0;i<idList.size();i++){
            StringTag tag= (StringTag) idList.get(i);
            moduleIds[i]=new ResourceLocation(tag.getAsString());
        }
        this.setModules(moduleIds);
        this.enable=nbt.getBoolean("enable");
        runEvent(TkkAiModuleEventName.DESERIALIZE_NBT,new Object[]{nbt});
        if(enable){
            runEvent(TkkAiModuleEventName.AI_ENABLE,new Object[0]);
        }
    }
}
