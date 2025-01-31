package tkk.epic.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class UpdatePlayerSkillEvent extends PlayerEvent {
    public String[] skills;
    public UpdatePlayerSkillEvent(Player player,String[] skills) {
        super(player);
        this.skills=skills;
    }


}
