package tkk.epic.key;

import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;
import tkk.epic.TkkEpic;
import tkk.epic.key.key.SpellKeybinding;
import tkk.epic.key.key.SpellSwitchHotbarPos;

public class KeybindingsManager {
    public static SpellKeybinding SPELL_KEYBINDING_0=new SpellKeybinding(TkkEpic.MODID+".spell1", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_1,TkkEpic.MODID+".tkkgame",0);
    public static SpellKeybinding SPELL_KEYBINDING_1=new SpellKeybinding(TkkEpic.MODID+".spell2", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_2,TkkEpic.MODID+".tkkgame",1);
    public static SpellKeybinding SPELL_KEYBINDING_2=new SpellKeybinding(TkkEpic.MODID+".spell3", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_3,TkkEpic.MODID+".tkkgame",2);
    public static SpellKeybinding SPELL_KEYBINDING_3=new SpellKeybinding(TkkEpic.MODID+".spell4", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_4,TkkEpic.MODID+".tkkgame",3);
    public static SpellKeybinding SPELL_KEYBINDING_4=new SpellKeybinding(TkkEpic.MODID+".spell5", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_5,TkkEpic.MODID+".tkkgame",4);
    public static SpellKeybinding SPELL_KEYBINDING_5=new SpellKeybinding(TkkEpic.MODID+".spell6", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_6,TkkEpic.MODID+".tkkgame",5);
    public static SpellKeybinding SPELL_KEYBINDING_6=new SpellKeybinding(TkkEpic.MODID+".spell7", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_7,TkkEpic.MODID+".tkkgame",6);
    public static SpellKeybinding SPELL_KEYBINDING_7=new SpellKeybinding(TkkEpic.MODID+".spell8", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, GLFW.GLFW_KEY_8,TkkEpic.MODID+".tkkgame",7);
    public static SpellSwitchHotbarPos SPELL_SWITCH_HOTBAR_POS=new SpellSwitchHotbarPos(TkkEpic.MODID+".switch", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_H,TkkEpic.MODID+".tkkgame");
    public static SpellKeybinding getSpellKeybindingFromInt(int num){
        if(num==0){return SPELL_KEYBINDING_0;}
        if(num==1){return SPELL_KEYBINDING_1;}
        if(num==2){return SPELL_KEYBINDING_2;}
        if(num==3){return SPELL_KEYBINDING_3;}
        if(num==4){return SPELL_KEYBINDING_4;}
        if(num==5){return SPELL_KEYBINDING_5;}
        if(num==6){return SPELL_KEYBINDING_6;}
        if(num==7){return SPELL_KEYBINDING_7;}
        return null;
    }
    public static void keybindingsRegister(){
        KeyEventLoader.registerKeyBinding("spell0",SPELL_KEYBINDING_0);
        KeyEventLoader.registerKeyBinding("spell1",SPELL_KEYBINDING_1);
        KeyEventLoader.registerKeyBinding("spell2",SPELL_KEYBINDING_2);
        KeyEventLoader.registerKeyBinding("spell3",SPELL_KEYBINDING_3);
        KeyEventLoader.registerKeyBinding("spell4",SPELL_KEYBINDING_4);
        KeyEventLoader.registerKeyBinding("spell5",SPELL_KEYBINDING_5);
        KeyEventLoader.registerKeyBinding("spell6",SPELL_KEYBINDING_6);
        KeyEventLoader.registerKeyBinding("spell7",SPELL_KEYBINDING_7);
        KeyEventLoader.registerKeyBinding("switch",SPELL_SWITCH_HOTBAR_POS);
    }

}
