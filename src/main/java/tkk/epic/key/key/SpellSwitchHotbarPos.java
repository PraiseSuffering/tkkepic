package tkk.epic.key.key;

import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import tkk.epic.gui.hud.hotbar.HotBarManager;
import tkk.epic.key.TkkKeyBinding;

public class SpellSwitchHotbarPos extends TkkKeyBinding {
    public SpellSwitchHotbarPos(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }

    @Override
    public void pressStart() {
        if(HotBarManager.hotBarPos==0){
            HotBarManager.hotBarPos=1;
        }else{
            HotBarManager.hotBarPos=0;
        }
    }

    @Override
    public void pressTick() {
    }

    @Override
    public void pressOver() {
    }
}
