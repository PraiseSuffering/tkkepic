package tkk.epic.capability.ai.module;

import net.minecraft.resources.ResourceLocation;
import tkk.epic.capability.ai.ITkkAiCapability;
import tkk.epic.js.JsContainer;

public class JsAiModule implements TkkAiModule{
    public final ResourceLocation id;
    public final JsContainer jsContainer;

    public JsAiModule(ResourceLocation id,JsContainer js){
        this.id=id;
        this.jsContainer=js;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void runEvent(boolean moduleIsEnable, ITkkAiCapability entity, String event, Object[] args) {
        Object[] temp=new Object[args.length+2];
        temp[0]=moduleIsEnable;
        temp[1]=entity;
        for (int i=0;i<args.length;i++){
            temp[i+2]=args[i];
        }
        jsContainer.run(event,temp);
    }

    @Override
    public int getExpect(boolean moduleIsEnable, ITkkAiCapability entity) {
        Object expect= jsContainer.run("getExpect",moduleIsEnable,entity);
        if (expect==null || !(expect instanceof Integer)){return 0;}
        return (Integer)expect;
    }

    @Override
    public void onEnable(ITkkAiCapability entity) {
        runEvent(true,entity,"onEnable",new Object[0]);
    }

    @Override
    public void onDisable(ITkkAiCapability entity) {
        runEvent(false,entity,"onDisable",new Object[0]);

    }
}
