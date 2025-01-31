package tkk.epic.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import tkk.epic.TkkEpic;
import tkk.epic.skill.EquipSkillHandle;
import tkk.epic.skill.SkillItemType;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.main.EpicFightMod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_SKILL_PRIORITY;
    static {
        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
        ITEM_SKILL_PRIORITY = server.defineList("ITEM_SKILL_PRIORITY", Arrays.asList("Feet","Legs","Chest","Head","OffHand","Curios","MainHand"), (element) -> {
            if (element instanceof String str) {
                return SkillItemType.getSkillItemType(str) !=null;
            }

            return false;
        });
        CLIENT_CONFIG = client.build();
        COMMON_CONFIG = server.build();
    }
    public static void regConfig(){
        /*
        CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(TkkEpic.MODID+".toml").toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        COMMON_CONFIG.setConfig(file);
        file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(TkkEpic.MODID+"-client.toml").toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        CLIENT_CONFIG.setConfig(file);
         */
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }
}
