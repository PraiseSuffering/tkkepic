package tkk.epic.capability.ai.module;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.Level;
import tkk.epic.TkkEpic;
import tkk.epic.event.RegisterAiModuleEvent;
import tkk.epic.js.JsContainer;
import tkk.epic.skill.Skills;
import tkk.epic.skill.skills.js.CustomJsSkill;
import tkk.epic.utils.FileTool;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TkkAiModuleManager {
    public static Map<ResourceLocation,TkkAiModule> MODULES=new HashMap<>();
    public static TkkAiModule getModule(ResourceLocation id){
        return TkkAiModuleManager.MODULES.get(id);
    };
    public static void regModule(ResourceLocation id,TkkAiModule obj){
        if (TkkAiModuleManager.MODULES.containsKey(id)){
            TkkEpic.LOGGER.warn("[TkkAI] Duplicate id:"+id);
        }
        TkkAiModuleManager.MODULES.put(id,obj);
    }
    public static void createRegistry(NewRegistryEvent event) {
        event.create(RegistryBuilder.of(new ResourceLocation(TkkEpic.MODID, "ai_module")));
    }
    public static void reg(){
        loadModules();
    }
    public static void loadModules(){
        ModLoader.get().postEvent(new RegisterAiModuleEvent(MODULES));
        loadCustomJsModules();
    }
    public static void loadCustomJsModules(){
        try {
            Map<String,String> jsText = FileTool.getPluginJs(new File(TkkEpic.MOD_DIR.getCanonicalPath() + "/CustomAiModule/"));
            for (String fileName:jsText.keySet()){
                JsContainer jsContainer=new JsContainer(jsText.get(fileName));
                if(jsContainer.errored){
                    TkkEpic.getInstance().broadcast("§cJsAiModule "+fileName+" error:§f "+jsContainer.print);
                }
                jsContainer.errorPrint= TkkAiModuleManager::JsAiModuleErrorPrint;
                ResourceLocation id=new ResourceLocation(TkkEpic.MODID,fileName.substring(0,fileName.length()-3));
                regModule(id,new JsAiModule(id,jsContainer));
            }
        }catch (Exception e){
            TkkEpic.LOGGER.log(Level.ERROR,"JsAiModule error:"+e);
        }
    }


    public static void JsAiModuleErrorPrint(JsContainer jsContainer){
        TkkEpic.getInstance().broadcast("§cJsAiModule error:§f "+jsContainer.print);
    }


}
