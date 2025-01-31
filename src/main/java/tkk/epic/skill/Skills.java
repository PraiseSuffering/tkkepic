package tkk.epic.skill;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import org.apache.logging.log4j.Level;
import tkk.epic.TkkEpic;
import tkk.epic.event.RegisterAiModuleEvent;
import tkk.epic.event.RegisterTkkSkillEvent;
import tkk.epic.item.CreativeTab;
import tkk.epic.js.JsContainer;
import tkk.epic.skill.skills.EmptySkill;
import tkk.epic.skill.skills.js.CustomJsSkill;
import tkk.epic.utils.FileTool;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skills {
    public static final HashMap<String, Skill> SKILL_HASH_MAP=new HashMap<>();
    public static Skill EMPTY_SKILL;
    public static void reg(){
        EMPTY_SKILL=new EmptySkill();
        regSkill(EMPTY_SKILL);
        loadSkill();
    }
    @Nullable
    public static Skill getSkill(String id){
        return SKILL_HASH_MAP.getOrDefault(id,EMPTY_SKILL);
    }
    public static void regSkill(Skill obj){
        SKILL_HASH_MAP.put(obj.getSkillId(),obj);
    }
    public static void loadSkill(){
        ModLoader.get().postEvent(new RegisterTkkSkillEvent());
        loadCustomJsSkills();
    }
    public static void loadCustomJsSkills(){
        try {
            Map<String,String> jsText = FileTool.getPluginJs(new File(TkkEpic.MOD_DIR.getCanonicalPath() + "/CustomJsSkills/"));
            for (String fileName:jsText.keySet()){
                if(SKILL_HASH_MAP.get(fileName)!=null && SKILL_HASH_MAP.get(fileName) instanceof CustomJsSkill &&
                        ((CustomJsSkill) SKILL_HASH_MAP.get(fileName)).jsContainer.js.equals(jsText.get(fileName))){continue;}
                JsContainer jsContainer=new JsContainer(jsText.get(fileName));
                if(jsContainer.errored){
                    TkkEpic.getInstance().broadcast("§cJsSkillFile "+fileName+" error:§f "+jsContainer.print);
                }
                jsContainer.errorPrint=Skills::JsSkillErrorPrint;
                regSkill(new CustomJsSkill(jsContainer));
            }
        }catch (Exception e){
            TkkEpic.LOGGER.log(Level.ERROR,"loadCustomJsSkills error:"+e);
        }
    }
    public static void regCustomJsSkill(String jsCode) throws Exception{
        JsContainer jsContainer=new JsContainer(jsCode);
        if(jsContainer.errored){Skills.JsSkillErrorPrint(jsContainer);}
        jsContainer.errorPrint=Skills::JsSkillErrorPrint;
        File file=new File(TkkEpic.MOD_DIR.getCanonicalPath() + "/CustomJsSkills/"+jsContainer.run("getSkillId")+".js");
        FileTool.putJs(file,jsCode);
        regSkill(new CustomJsSkill(jsContainer));

    }
    public static void JsSkillErrorPrint(JsContainer jsContainer){
        TkkEpic.getInstance().broadcast("§cJsSkill error:§f "+jsContainer.print);
    }
}
