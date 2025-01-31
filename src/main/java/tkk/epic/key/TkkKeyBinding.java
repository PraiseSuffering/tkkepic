package tkk.epic.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public abstract class TkkKeyBinding {
    public KeyMapping keyBinding;
    public boolean lastKeyDown=false;
    public TkkKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category){
        keyBinding=new KeyMapping(description,keyConflictContext,keyModifier, InputConstants.Type.KEYSYM,keyCode,category);
    }

    public TkkKeyBinding(String name, String usableRange, String AttachedKey, int keyCode, String tab){
        IKeyConflictContext tempA;
        KeyModifier tempB;
        switch (usableRange){
            case "GUI":
                tempA=KeyConflictContext.GUI;
                break;
            case "INGAME":
                tempA=KeyConflictContext.IN_GAME;
                break;
            case "ALL":
                tempA=KeyConflictContext.UNIVERSAL;
                break;
            default:
                tempA=KeyConflictContext.UNIVERSAL;
        }
        switch (AttachedKey){
            case "CTRL":
                tempB=KeyModifier.CONTROL;
                break;
            case "ALT":
                tempB=KeyModifier.ALT;
                break;
            case "SHIFT":
                tempB=KeyModifier.SHIFT;
                break;
            case "NONE":
                tempB=KeyModifier.NONE;
                break;
            default:
                tempB=KeyModifier.NONE;
        }
        keyBinding=new KeyMapping(name,tempA,tempB,InputConstants.Type.KEYSYM,keyCode,tab);
    }


    public boolean isPressed(){return keyBinding.consumeClick();}

    public boolean isKeyDown(){
        return keyBinding.isDown();
    }

    public abstract void pressStart();

    public abstract void pressTick();

    public abstract void pressOver();

}
