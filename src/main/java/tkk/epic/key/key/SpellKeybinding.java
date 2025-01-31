package tkk.epic.key.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import tkk.epic.gui.hud.hotbar.HotBarManager;
import tkk.epic.key.TkkKeyBinding;
import tkk.epic.network.CPTkkSpellPress;
import tkk.epic.network.CPTkkSpellUp;
import tkk.epic.network.TkkEpicNetworkManager;

public class SpellKeybinding extends TkkKeyBinding {
    public int spellId;
    public SpellKeybinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category, int spellId) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
        this.spellId=spellId;
    }

    @Override
    public void pressStart() {
        if(HotBarManager.spells[spellId].isDisable || !HotBarManager.spells[spellId].doRender || HotBarManager.spells[spellId].mana>0 || HotBarManager.spells[spellId].cooldown>0){return;}

        Input input = Minecraft.getInstance().player.input;
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(Minecraft.getInstance().player), 0.0F, 1.0F);
        input.tick(false, pulse);
        TkkEpicNetworkManager.sendToServer(new CPTkkSpellPress(spellId, HotBarManager.spells[spellId].isEnable,input.up,input.down,input.left,input.right));
    }

    @Override
    public void pressTick() {

    }

    @Override
    public void pressOver() {
        if(HotBarManager.spells[spellId].isDisable || !HotBarManager.spells[spellId].doRender || HotBarManager.spells[spellId].mana>0 || HotBarManager.spells[spellId].cooldown>0){return;}

        Input input = Minecraft.getInstance().player.input;
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(Minecraft.getInstance().player), 0.0F, 1.0F);
        input.tick(false, pulse);
        TkkEpicNetworkManager.sendToServer(new CPTkkSpellUp(spellId, HotBarManager.spells[spellId].isEnable,input.up,input.down,input.left,input.right));
    }
}
