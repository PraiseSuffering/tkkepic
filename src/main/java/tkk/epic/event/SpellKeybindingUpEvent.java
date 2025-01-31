package tkk.epic.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class SpellKeybindingUpEvent extends Event {
    public final int spellId;
    public final boolean enable;
    public final ServerPlayer player;
    public final boolean forward;
    public final boolean backward;
    public final boolean left;
    public final boolean right;




    public SpellKeybindingUpEvent(ServerPlayer player, int id, boolean isEnable,boolean forward,boolean backward,boolean left,boolean right){
        this.player = player;
        this.spellId = id;
        this.enable = isEnable;
        this.forward=forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
    }
}
